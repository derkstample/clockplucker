import os
import re
import requests
import html
import hashlib

# Constants for paths and URLs
SCRAPED_IMAGES_DIR = 'app/src/main/res/drawable/'
STRINGS_FILE_DIR = 'app/src/main/res/values/'
STRINGS_FILE_NAME = 'character_strings.xml'
JINXES_FILE_NAME = 'jinx_strings.xml'
BOTC_WIKI_BASE_URL = "https://wiki.bloodontheclocktower.com"

# Ensure the directory for scraped data exists
os.makedirs(SCRAPED_IMAGES_DIR, exist_ok=True)
os.makedirs(STRINGS_FILE_DIR, exist_ok=True)

def process_name_for_id(name):
    """Cleans a name for use in XML IDs (lowercase, alphanumeric only)."""
    decoded = html.unescape(name)
    return re.sub(r"[^a-zA-Z]", "", decoded).lower()

def process_character_name(name):
    """
    Decodes HTML entities and generates cleaned IDs and Wiki-compatible titles.
    """
    # Fix HTML entities like &#039; -> '
    decoded_name = html.unescape(name)
    decoded_name = decoded_name.replace('\'','\\')
    # Cleaned: lowercase, no special characters (for file names/XML IDs)
    cleaned = re.sub(r"[^a-zA-Z]", "", decoded_name).lower()
    # Wiki Title: URL friendly version
    wiki_title = decoded_name.replace(" ", "_")
    return {
        'original': decoded_name,
        'cleaned': cleaned,
        'wiki_title': wiki_title
    }
    
def fetch_character_types_from_wiki():
    standard_types = []
    special_types = []
    url = f"{BOTC_WIKI_BASE_URL}/index.php?title=Main_Page&action=edit"
    try:
        res = requests.get(url)
        res.raise_for_status()
        pattern = r'\[\[File:generic_[^"]+.png\|250px\|thumb\|center\|link=Category:([^"]+)\]\]'
        matches = re.findall(pattern, res.text)
        for category in matches:
            standard_types.append(category)
        pattern = r'\[\[File:generic_[^"]+.png\|250px\|thumb\|center\|link=([^"]+)\]\]'
        matches = re.findall(pattern, res.text)
        for category in matches:
            if "Category:" not in category:
                special_types.append(category)
    except Exception as e:
        print(f"Error fetching character categories: {e}")
    return standard_types, special_types

def fetch_characters_from_wiki():
    """Scrapes both standard categories and special type pages."""
    all_characters = []
    seen_names = set()
    
    print("Fetching character list from wiki...")
    
    # 0. Scrape character categories
    standard_types, special_types = fetch_character_types_from_wiki()

    # 1. Scrape Standard Categories
    for char_type in standard_types:
        url = f"{BOTC_WIKI_BASE_URL}/Category:{char_type}"
        try:
            res = requests.get(url)
            res.raise_for_status()
            # allow any character except double-quote inside title
            pattern = r'<li><a href="/[^"]+" title="([^"]+)">[^<]+</a></li>'
            matches = re.findall(pattern, res.text)
            for name in matches:
                char_data = process_character_name(name.strip())
                if ":" not in char_data['original'] and char_data['original'] not in seen_names:
                    all_characters.append(char_data)
                    seen_names.add(char_data['original'])
                    
            print(f"  Found {len(matches)} potential {char_type}.")
        except Exception as e:
            print(f"Error fetching {char_type}: {e}")

    # 2. Scrape Special Pages (Travellers, Fabled, Loric)
    for char_type in special_types:
        url = f"{BOTC_WIKI_BASE_URL}/index.php?title={char_type}&action=edit"
        try:
            res = requests.get(url)
            res.raise_for_status()
            
            pattern = r'\[\[File:icon_[^"]+.png\|250px\|thumb\|center\|link=([^"]+)\]\]'
            matches = re.findall(pattern, res.text)
            for name in matches:
                char_data = process_character_name(name.strip())
                if char_data['original'] not in seen_names:
                    all_characters.append(char_data)
                    seen_names.add(char_data['original'])
            print(f"  Found {len(matches)} potential {char_type}.")
        except Exception as e:
            print(f"Error fetching special type {char_type}: {e}")

    print(f"Total unique characters found: {len(all_characters)}\n")
    return all_characters

def download_image(image_url, cleaned_name):
    """Download and save character icon."""
    try:
        response = requests.get(image_url)
        response.raise_for_status()
        scraped_file = f'{SCRAPED_IMAGES_DIR}icon_{cleaned_name}.png'
        with open(scraped_file, "wb") as file:
            file.write(response.content)
        print(f"    [Image] Saved icon_{cleaned_name}.png")
    except Exception as e:
        print(f"    [Image] Failed to download {cleaned_name}: {e}")
        
def scrape_jinxes():
    """
    Scrapes the Djinn edit page and returns a dict: (char1, char2) -> description.
    Also saves to jinxes_strings.xml.
    """
    jinx_dict = {}
    url = f"{BOTC_WIKI_BASE_URL}/index.php?title=Djinn&action=edit"
    
    print("Scraping Jinxes...")
    try:
        res = requests.get(url)
        res.raise_for_status()
        
        # Regex captures: {{Type|Char1}} / {{Type|Char2}} : Description
        # We allow any character type inside the first part of the template
        pattern = r'^\*\s*\{\{[^|]+\|(?P<c1>[^}]+)\}\}\s*/\s*\{\{[^|]+\|(?P<c2>[^}]+)\}\}\s*:\s*(?P<desc>.+)$'
        matches = re.finditer(pattern, res.text, re.MULTILINE)

        with open(STRINGS_FILE_DIR + JINXES_FILE_NAME, 'w', encoding='utf-8') as f:
            f.write('<?xml version="1.0" encoding="utf-8"?>\n<resources>\n')
            
            for match in matches:
                char1 = html.unescape(match.group('c1')).strip()
                char2 = html.unescape(match.group('c2')).strip()
                description = html.unescape(match.group('desc')).strip()
                
                # Store in dictionary
                jinx_dict[(char1, char2)] = description
                
                # Format for XML
                id1 = process_name_for_id(char1)
                id2 = process_name_for_id(char2)
                xml_safe_desc = description.replace("&", '&amp;')
                xml_safe_desc = xml_safe_desc.replace('"', '&quot;')
                xml_safe_desc = xml_safe_desc.replace("'", "\\'")
                xml_safe_desc = re.sub(r'\[([^\]]+)\]', r'<b>[\1]</b>', xml_safe_desc)
                
                f.write(f'    <string name="jinx_{id1}_{id2}">{xml_safe_desc}</string>\n')
            
            f.write('</resources>')
        
        print(f"  Successfully saved {len(jinx_dict)} jinxes to {JINXES_FILE_NAME}")

    except Exception as e:
        print(f"  Error scraping jinxes: {e}")

def scrape_character_data(characters):
    """Scrapes images and abilities using updated regex for smart quotes."""
    character_abilities = []
    character_names = []

    for char in characters:
        print(f"Processing: {char['original']}")
        
        # --- 0. Append Name ---
        xml_entry = f'    <string name="name_{char["cleaned"]}"><b>{char["original"]}</b></string>'
        character_names.append(xml_entry)
        
        # --- 1. Scrape Ability Text ---
        edit_url = f"{BOTC_WIKI_BASE_URL}/index.php?title={char['wiki_title']}&action=edit"
        try:
            res = requests.get(edit_url)
            res.raise_for_status()
            
            # Looks for == Summary == followed by a newline and text inside any quote type
            summary_match = re.search(r"== Summary ==\s*\n\s*[\"“](.*?)[\"”]", res.text)
            
            if summary_match:
                ability_text = summary_match.group(1)
                ability_text = ability_text.replace("'", "\\'")
                ability_text = re.sub(r'\[([^\]]+)\]', r'<b>\[\1\]</b>', ability_text)
                xml_entry = f'    <string name="ability_{char["cleaned"]}">{ability_text}</string>'
                character_abilities.append(xml_entry)
                print(f"    [Text] Successfully extracted ability.")
            else:
                print(f"    [Text] Warning: No summary found for {char['original']}.")
        except Exception as e:
            print(f"    [Text] Error fetching text: {e}")

        # --- 2. Scrape Image ---
        try:
            if hashlib.sha256(char['cleaned'].encode()).hexdigest() == "1f9a614e14dec6e3d9ec042b0b303c5df072be002b36d2d0e8442029d121280c":
                temp = char['cleaned'][:3] + "_" + char['cleaned'][3:]
                img_page_url = f"{BOTC_WIKI_BASE_URL}/File:Icon_{temp}.png"
            else:
                img_page_url = f"{BOTC_WIKI_BASE_URL}/File:Icon_{char['cleaned']}.png"
            img_res = requests.get(img_page_url)
            img_res.raise_for_status()
            
            pattern = r'href="(/images/[^/]+/[^/]+/Icon_[^"]+.png)"'
            matches = re.findall(pattern, img_res.text)
            if matches:
                image_url = BOTC_WIKI_BASE_URL + matches[0]
                download_image(image_url, char['cleaned'])
            
        except Exception as e:
            print(f"    [Image] Failed to download {char['cleaned']}: {e}")

    save_xml(character_names, character_abilities)

def save_xml(names_list, abilities_list):
    """Saves to character_strings.xml."""
    with open(STRINGS_FILE_DIR + STRINGS_FILE_NAME, 'w', encoding='utf-8') as f:
        f.write('<?xml version="1.0" encoding="utf-8"?>\n')
        f.write('<resources>\n')
        for name in names_list:
            f.write(name + '\n')
        for line in abilities_list:
            f.write(line + '\n')
        f.write('</resources>')
    print(f"\nSaved {len(abilities_list)} abilities to {STRINGS_FILE_NAME}")

if __name__ == "__main__":
    character_list = fetch_characters_from_wiki()
    if character_list:
        scrape_character_data(character_list)
    scrape_jinxes()
    
