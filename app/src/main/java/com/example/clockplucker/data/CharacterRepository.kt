package com.example.clockplucker.data

import com.example.clockplucker.R

object CharacterRepository {
    private val characterData = mapOf(
        "acrobat" to Character(
            name = "Acrobat",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_acrobat,
            ability = "Each night*, choose a player: if they are or become drunk or poisoned tonight, you die."
        ),
        "alchemist" to Character(
            name = "Alchemist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_alchemist,
            ability = "You have a Minion ability. When using this, the Storyteller may prompt you to choose differently."
        ),
        "alsaahir" to Character(
            name = "Alsaahir",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_alsaahir,
            ability = "Each day, if you publicly guess which players are Minion(s) and which are Demon(s), good wins."
        ),
        "amnesiac" to Character(
            name = "Amnesiac",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_amnesiac,
            ability = "You do not know what your ability is. Each day, privately guess what it is: you learn how accurate you are."
        ),
        "artist" to Character(
            name = "Artist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_artist,
            ability = "Once per game, during the day, privately ask the Storyteller any yes/no question."
        ),
        "atheist" to Character(
            name = "Atheist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_atheist,
            ability = "The Storyteller can break the game rules, and if executed, good wins, even if you are dead. [No evil characters]"
        ),
        "balloonist" to Character(
            name = "Balloonist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_balloonist,
            ability = "Each night, you learn a player of a different character type than last night. [+0 or +1 Outsider]"
        ),
        "banshee" to Character(
            name = "Banshee",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_banshee,
            ability = "If the Demon kills you, all players learn this. From now on, you may nominate twice per day and vote twice per nomination."
        ),
        "bountyhunter" to Character(
            name = "Bounty Hunter",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_bountyhunter,
            ability = "You start knowing 1 evil player. If the player you know dies, you learn another evil player tonight. [1 Townsfolk is evil]"
        ),
        "cannibal" to Character(
            name = "Cannibal",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_cannibal,
            ability = "You have the ability of the recently killed executee. If they are evil, you are poisoned until a good player dies by execution."
        ),
        "chambermaid" to Character(
            name = "Chambermaid",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_chambermaid,
            ability = "Each night, choose 2 alive players (not yourself): you learn how many woke tonight due to their ability."
        ),
        "chef" to Character(
            name = "Chef",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_chef,
            ability = "You start knowing how many pairs of evil players there are."
        ),
        "choirboy" to Character(
            name = "Choirboy",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_choirboy,
            ability = "If the Demon kills the King, you learn which player is the Demon. [+the King]"
        ),
        "clockmaker" to Character(
            name = "Clockmaker",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_clockmaker,
            ability = "You start knowing how many steps from the Demon to its nearest Minion."
        ),
        "courtier" to Character(
            name = "Courtier",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_courtier,
            ability = "Once per game, at night, choose a character: they are drunk for 3 nights & 3 days."
        ),
        "cultleader" to Character(
            name = "Cult Leader",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_cultleader,
            ability = "Each night, you become the alignment of an alive neighbor. If all good players choose to join your cult, your team wins."
        ),
        "dreamer" to Character(
            name = "Dreamer",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_dreamer,
            ability = "Each night, choose a player (not yourself or Travellers): you learn 1 good & 1 evil character, 1 of which is correct."
        ),
        "empath" to Character(
            name = "Empath",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_empath,
            ability = "Each night, you learn how many of your 2 alive neighbors are evil."
        ),
        "engineer" to Character(
            name = "Engineer",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_engineer,
            ability = "Once per game, at night, choose which Minions or which Demon is in play."
        ),
        "exorcist" to Character(
            name = "Exorcist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_exorcist,
            ability = "Each night*, choose a player (different to last night): the Demon, if chosen, learns who you are then doesn't wake tonight."
        ),
        "farmer" to Character(
            name = "Farmer",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_farmer,
            ability = "When you die at night, an alive good player becomes a Farmer."
        ),
        "fisherman" to Character(
            name = "Fisherman",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fisherman,
            ability = "Once per game, during the day, visit the Storyteller for some advice to help your team win."
        ),
        "flowergirl" to Character(
            name = "Flowergirl",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_flowergirl,
            ability = "Each night*, you learn if a Demon voted today."
        ),
        "fool" to Character(
            name = "Fool",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fool,
            ability = "The 1st time you die, you don't."
        ),
        "fortuneteller" to Character(
            name = "Fortune Teller",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fortuneteller,
            ability = "Each night, choose 2 players: you learn if either is a Demon. There is a good player that registers as a Demon to you."
        ),
        "gambler" to Character(
            name = "Gambler",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_gambler,
            ability = "Each night*, choose a player & guess their character: if you guess wrong, you die."
        ),
        "general" to Character(
            name = "General",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_general,
            ability = "Each night, you learn which alignment the Storyteller believes is winning: good, evil, or neither."
        ),
        "gossip" to Character(
            name = "Gossip",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_gossip,
            ability = "Each day, you may make a public statement. Tonight, if it was true, a player dies."
        ),
        "grandmother" to Character(
            name = "Grandmother",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_grandmother,
            ability = "You start knowing a good player & their character. If the Demon kills them, you die too."
        ),
        "highpriestess" to Character(
            name = "High Priestess",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_highpriestess,
            ability = "Each night, learn which player the Storyteller believes you should talk to most."
        ),
        "huntsman" to Character(
            name = "Huntsman",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_huntsman,
            ability = "Once per game, at night, choose a living player: the Damsel, if chosen, becomes a not-in-play Townsfolk. [+the Damsel]"
        ),
        "innkeeper" to Character(
            name = "Innkeeper",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_innkeeper,
            ability = "Each night*, choose 2 players: they can't die tonight, but 1 is drunk until dusk."
        ),
        "investigator" to Character(
            name = "Investigator",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_investigator,
            ability = "You start knowing that 1 of 2 players is a particular Minion."
        ),
        "juggler" to Character(
            name = "Juggler",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_juggler,
            ability = "On your 1st day, publicly guess up to 5 players' characters. That night, you learn how many you got correct."
        ),
        "king" to Character(
            name = "King",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_king,
            ability = "Each night, if the dead equal or outnumber the living, you learn 1 alive character. The Demon knows you are the King."
        ),
        "knight" to Character(
            name = "Knight",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_knight,
            ability = "You start knowing 2 players that are not the Demon."
        ),
        "librarian" to Character(
            name = "Librarian",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_librarian,
            ability = "You start knowing that 1 of 2 players is a particular Outsider. (Or that zero are in play.)"
        ),
        "lycanthrope" to Character(
            name = "Lycanthrope",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_lycanthrope,
            ability = "Each night*, choose an alive player. If good, they die & the Demon doesn’t kill tonight. One good player registers as evil."
        ),
        "magician" to Character(
            name = "Magician",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_magician,
            ability = "The Demon thinks you are a Minion. Minions think you are a Demon."
        ),
        "mathematician" to Character(
            name = "Mathematician",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_mathematician,
            ability = "Each night, you learn how many players’ abilities worked abnormally (since dawn) due to another character's ability."
        ),
        "mayor" to Character(
            name = "Mayor",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_mayor,
            ability = "If only 3 players live & no execution occurs, your team wins. If you die at night, another player might die instead."
        ),
        "minstrel" to Character(
            name = "Minstrel",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_minstrel,
            ability = "When a Minion dies by execution, all other players (except Travellers) are drunk until dusk tomorrow."
        ),
        "monK" to Character(
            name = "Monk",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_monk,
            ability = "Each night*, choose a player (not yourself): they are safe from the Demon tonight."
        ),
        "nightwatchman" to Character(
            name = "Nightwatchman",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_nightwatchman,
            ability = "Once per game, at night, choose a player: they learn you are the Nightwatchman."
        ),
        "noble" to Character(
            name = "Noble",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_noble,
            ability = "You start knowing 3 players, 1 and only 1 of which is evil."
        ),
        "oracle" to Character(
            name = "Oracle",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_oracle,
            ability = "Each night*, you learn how many dead players are evil."
        ),
        "pacifist" to Character(
            name = "Pacifist",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_pacifist,
            ability = "Executed good players might not die."
        ),
        "philosopher" to Character(
            name = "Philosopher",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_philosopher,
            ability = "Once per game, at night, choose a good character: gain that ability. If this character is in play, they are drunk."
        ),
        "pixie" to Character(
            name = "Pixie",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_pixie,
            ability = "You start knowing 1 in-play Townsfolk. If you were mad that you were this character, you gain their ability when they die."
        ),
        "poppygrower" to Character(
            name = "Poppy Grower",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_poppygrower,
            ability = "Minions & Demons do not know each other. If you die, they learn who each other are that night."
        ),
        "preacher" to Character(
            name = "Preacher",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_preacher,
            ability = "Each night, choose a player: a Minion, if chosen, learns this. All chosen Minions have no ability."
        ),
        "princess" to Character(
            name = "Princess",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_princess,
            ability = "On your 1st day, if you nominated & executed a player, the Demon doesn't kill tonight."
        ),
        "professor" to Character(
            name = "Professor",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_professor,
            ability = "Once per game, at night*, choose a dead player: if they are a Townsfolk, they are resurrected."
        ),
        "ravenkeeper" to Character(
            name = "Ravenkeeper",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_ravenkeeper,
            ability = "If you die at night, you are woken to choose a player: you learn their character."
        ),
        "sage" to Character(
            name = "Sage",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_sage,
            ability = "If the Demon kills you, you learn that it is 1 of 2 players."
        ),
        "sailor" to Character(
            name = "Sailor",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_sailor,
            ability = "Each night, choose an alive player: either you or they are drunk until dusk. You can't die."
        ),
        "savant" to Character(
            name = "Savant",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_savant,
            ability = "Each day, you may visit the Storyteller to learn 2 things in private: 1 is true & 1 is false."
        ),
        "seamstress" to Character(
            name = "Seamstress",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_seamstress,
            ability = "Once per game, at night, choose 2 players (not yourself): you learn if they are the same alignment."
        ),
        "shugenja" to Character(
            name = "Shugenja",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_shugenja,
            ability = "You start knowing if your closest evil player is clockwise or anti-clockwise. If equidistant, this info is arbitrary."
        ),
        "slayer" to Character(
            name = "Slayer",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_slayer,
            ability = "Once per game, during the day, publicly choose a player: if they are the Demon, they die."
        ),
        "snakecharmer" to Character(
            name = "Snake Charmer",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_snakecharmer,
            ability = "Each night, choose an alive player: a chosen Demon swaps characters & alignments with you & is then poisoned."
        ),
        "soldier" to Character(
            name = "Soldier",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_soldier,
            ability = "You are safe from the Demon."
        ),
        "steward" to Character(
            name = "Steward",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_steward,
            ability = "You start knowing 1 good player."
        ),
        "tealady" to Character(
            name = "Tea Lady",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_tealady,
            ability = "If both your alive neighbors are good, they can't die."
        ),
        "towncrier" to Character(
            name = "Town Crier",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_towncrier,
            ability = "Each night*, you learn if a Minion nominated today."
        ),
        "undertaker" to Character(
            name = "Undertaker",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_undertaker,
            ability = "Each night*, you learn which character died by execution today."
        ),
        "villageidiot" to Character(
            name = "Village Idiot",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_villageidiot,
            ability = "Each night, choose a player: you learn their alignment. [+0 to +2 Village Idiots. 1 of the extras is drunk]"
        ),
        "virgin" to Character(
            name = "Virgin",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_virgin,
            ability = "The 1st time you are nominated, if the nominator is a Townsfolk, they are executed immediately."
        ),
        "washerwoman" to Character(
            name = "Washerwoman",
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_washerwoman,
            ability = "You start knowing that 1 of 2 players is a particular Townsfolk."
        ),
        // OUTSIDERS //
        "barber" to Character(
            name = "Barber",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_barber,
            ability = "If you died today or tonight, the Demon may choose 2 players (not another Demon) to swap characters."
        ),
        "butler" to Character(
            name = "Butler",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_butler,
            ability = "Each night, choose a player (not yourself): tomorrow, you may only vote if they are voting too."
        ),
        "damsel" to Character(
            name = "Damsel",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_damsel,
            ability = "All Minions know a Damsel is in play. If a Minion publicly guesses you (once), your team loses."
        ),
        "drunk" to Character(
            name = "Drunk",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_drunk,
            ability = "You do not know you are the Drunk. You think you are a Townsfolk character, but you are not."
        ),
        "golem" to Character(
            name = "Golem",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_golem,
            ability = "You may only nominate once per game. When you do, if the nominee is not the Demon, they die."
        ),
        "goon" to Character(
            name = "Goon",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_goon,
            ability = "Each night, the 1st player to choose you with their ability is drunk until dusk. You become their alignment."
        ),
        "hatter" to Character(
            name = "Hatter",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_hatter,
            ability = "If you died today or tonight, the Minion & Demon players may choose new Minion & Demon characters to be."
        ),
        "heretic" to Character(
            name = "Heretic",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_heretic,
            ability = "Whoever wins, loses & whoever loses, wins, even if you are dead."
        ),
        "hermit" to Character(
            name = "Hermit",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_heretic,
            ability = "You have all Outsider abilities. [-0 or -1 Outsider]"
        ),
        "klutz" to Character(
            name = "Klutz",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_klutz,
            ability = "When you learn that you died, publicly choose 1 alive player: if they are evil, your team loses."
        ),
        "lunatic" to Character(
            name = "Lunatic",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_lunatic,
            ability = "You think you are a Demon, but you are not. The Demon knows who you are & who you choose at night."
        ),
        "moonchild" to Character(
            name = "Moonchild",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_moonchild,
            ability = "When you learn that you died, publicly choose 1 alive player. Tonight, if it was a good player, they die."
        ),
        "mutant" to Character(
            name = "Mutant",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_mutant,
            ability = "If you are “mad” about being an Outsider, you might be executed."
        ),
        "ogre" to Character(
            name = "Ogre",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_ogre,
            ability = "On your 1st night, choose a player (not yourself): you become their alignment (you don't know which) even if drunk or poisoned."
        ),
        "plaguedoctor" to Character(
            name = "Plague Doctor",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_plaguedoctor,
            ability = "When you die, the Storyteller gains a Minion ability."
        ),
        "politician" to Character(
            name = "Politician",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_politician,
            ability = "If you were the player most responsible for your team losing, you change alignment & win, even if dead."
        ),
        "puzzlemaster" to Character(
            name = "Puzzlemaster",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_puzzlemaster,
            ability = "1 player is drunk, even if you die. If you guess (once) who it is, learn the Demon player, but guess wrong & get false info."
        ),
        "recluse" to Character(
            name = "Recluse",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_recluse,
            ability = "You might register as evil & as a Minion or Demon, even if dead."
        ),
        "saint" to Character(
            name = "Saint",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_saint,
            ability = "If you die by execution, your team loses."
        ),
        "snitch" to Character(
            name = "Snitch",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_snitch,
            ability = "Each Minion gets 3 bluffs."
        ),
        "sweetheart" to Character(
            name = "Sweetheart",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_sweetheart,
            ability = "When you die, 1 player is drunk from now on."
        ),
        "tinker" to Character(
            name = "Tinker",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_tinker,
            ability = "You might die at any time."
        ),
        "zealot" to Character(
            name = "Zealot",
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_zealot,
            ability = "If there are 5 or more players alive, you must vote for every nomination."
        ),
        // MINIONS //
        "assassin" to Character(
            name = "Assassin",
            type = CharType.MINION,
            icon = R.drawable.icon_assassin,
            ability = "Once per game, at night*, choose a player: they die, even if for some reason they could not."
        ),
        "baron" to Character(
            name = "Baron",
            type = CharType.MINION,
            icon = R.drawable.icon_baron,
            ability = "There are extra Outsiders in play. [+2 Outsiders]"
        ),
        "boffin" to Character(
            name = "Boffin",
            type = CharType.MINION,
            icon = R.drawable.icon_boffin,
            ability = "The Demon (even if drunk or poisoned) has a not-in-play good character’s ability. You both know which."
        ),
        "boomdandy" to Character(
            name = "Boomdandy",
            type = CharType.MINION,
            icon = R.drawable.icon_boomdandy,
            ability = "If you are executed, all but 3 players die. After a 10 to 1 countdown, the player with the most players pointing at them, dies."
        ),
        "cerenovus" to Character(
            name = "Cerenovus",
            type = CharType.MINION,
            icon = R.drawable.icon_cerenovus,
            ability = "Each night, choose a player & a good character: they are “mad” they are this character tomorrow, or might be executed."
        ),
        "devilsadvocate" to Character(
            name = "Devil's Advocate",
            type = CharType.MINION,
            icon = R.drawable.icon_devilsadvocate,
            ability = "Each night, choose a living player (different to last night): if executed tomorrow, they don't die."
        ),
        "eviltwin" to Character(
            name = "Evil Twin",
            type = CharType.MINION,
            icon = R.drawable.icon_eviltwin,
            ability = "You & an opposing player know each other. If the good player is executed, evil wins. Good can't win if you both live."
        ),
        "fearmonger" to Character(
            name = "Fearmonger",
            type = CharType.MINION,
            icon = R.drawable.icon_fearmonger,
            ability = "Each night, choose a player: if you nominate & execute them, their team loses. All players know if you choose a new player."
        ),
        "goblin" to Character(
            name = "Goblin",
            type = CharType.MINION,
            icon = R.drawable.icon_goblin,
            ability = "If you publicly claim to be the Goblin when nominated & are executed that day, your team wins."
        ),
        "godfather" to Character(
            name = "Godfather",
            type = CharType.MINION,
            icon = R.drawable.icon_godfather,
            ability = "You start knowing which Outsiders are in play. If 1 died today, choose a player tonight: they die. [-1 or +1 Outsider]"
        ),
        "harpy" to Character(
            name = "Harpy",
            type = CharType.MINION,
            icon = R.drawable.icon_harpy,
            ability = "Each night, choose 2 players: tomorrow, the 1st player is mad that the 2nd is evil, or one or both might die."
        ),
        "marionette" to Character(
            name = "Marionette",
            type = CharType.MINION,
            icon = R.drawable.icon_marionette,
            ability = "You think you are a good character, but you are not. The Demon knows who you are. [You neighbor the Demon]"
        ),
        "mastermind" to Character(
            name = "Mastermind",
            type = CharType.MINION,
            icon = R.drawable.icon_mastermind,
            ability = "If the Demon dies by execution (ending the game), play for 1 more day. If a player is then executed, their team loses."
        ),
        "mezepheles" to Character(
            name = "Mezepheles",
            type = CharType.MINION,
            icon = R.drawable.icon_mezepheles,
            ability = "You start knowing a secret word. The 1st good player to say this word becomes evil that night."
        ),
        "organgrinder" to Character(
            name = "Organ Grinder",
            type = CharType.MINION,
            icon = R.drawable.icon_organgrinder,
            ability = "All players keep their eyes closed when voting and the vote tally is secret. Each night, choose if you are drunk until dusk."
        ),
        "pithag" to Character(
            name = "Pit-Hag",
            type = CharType.MINION,
            icon = R.drawable.icon_pithag,
            ability = "Each night*, choose a player & a character they become (if not in play). If a Demon is made, deaths tonight are arbitrary."
        ),
        "poisoner" to Character(
            name = "Poisoner",
            type = CharType.MINION,
            icon = R.drawable.icon_poisoner,
            ability = "Each night, choose a player: they are poisoned tonight and tomorrow day."
        ),
        "psychopath" to Character(
            name = "Psychopath",
            type = CharType.MINION,
            icon = R.drawable.icon_psychopath,
            ability = "Each day, before nominations, you may publicly choose a player: they die. If executed, you only die if you lose roshambo."
        ),
        "scarletwoman" to Character(
            name = "Scarlet Woman",
            type = CharType.MINION,
            icon = R.drawable.icon_scarletwoman,
            ability = "If there are 5 or more players alive & the Demon dies, you become the Demon. (Travellers don't count.)"
        ),
        "spy" to Character(
            name = "Spy",
            type = CharType.MINION,
            icon = R.drawable.icon_spy,
            ability = "Each night, you see the Grimoire. You might register as good & as a Townsfolk or Outsider, even if dead."
        ),
        "summoner" to Character(
            name = "Summoner",
            type = CharType.MINION,
            icon = R.drawable.icon_summoner,
            ability = "You get 3 bluffs. On the 3rd night, choose a player: they become an evil Demon of your choice. [No Demon]"
        ),
        "vizier" to Character(
            name = "Vizier",
            type = CharType.MINION,
            icon = R.drawable.icon_vizier,
            ability = "All players know you are the Vizier. You cannot die during the day. If good voted, you may choose to execute immediately."
        ),
        "widow" to Character(
            name = "Widow",
            type = CharType.MINION,
            icon = R.drawable.icon_widow,
            ability = "On your 1st night, look at the Grimoire & choose a player: they are poisoned. 1 good player knows a Widow is in play."
        ),
        "witch" to Character(
            name = "Witch",
            type = CharType.MINION,
            icon = R.drawable.icon_witch,
            ability = "Each night, choose a player: if they nominate tomorrow, they die. If just 3 players live, you lose this ability."
        ),
        "wizard" to Character(
            name = "Wizard",
            type = CharType.MINION,
            icon = R.drawable.icon_wizard,
            ability = "Once per game, choose to make a wish. If granted, it might have a price & leave a clue as to its nature."
        ),
        "wraith" to Character(
            name = "Wraith",
            type = CharType.MINION,
            icon = R.drawable.icon_wraith,
            ability = "You may choose to open your eyes at night. You wake when other evil players do."
        ),
        "xaan" to Character(
            name = "Xaan",
            type = CharType.MINION,
            icon = R.drawable.icon_xaan,
            ability = "On night X, all Townsfolk are poisoned until dusk. [X Outsiders]"
        ),
        // DEMONS //
        "alhadikhia" to Character(
            name = "Al-Hadikhia",
            type = CharType.DEMON,
            icon = R.drawable.icon_alhadikhia,
            ability = "Each night*, you may choose 3 players (all players learn who): each silently chooses to live or die, but if all live, all die."
        ),
        "fanggu" to Character(
            name = "Fang Gu",
            type = CharType.DEMON,
            icon = R.drawable.icon_fanggu,
            ability = "Each night*, choose a player: they die. The 1st Outsider this kills becomes an evil Fang Gu & you die instead. [+1 Outsider]"
        ),
        "imp" to Character(
            name = "Imp",
            type = CharType.DEMON,
            icon = R.drawable.icon_imp,
            ability = "Each night*, choose a player: they die. If you kill yourself this way, a Minion becomes the Imp."
        ),
        "kazali" to Character(
            name = "Kazali",
            type = CharType.DEMON,
            icon = R.drawable.icon_kazali,
            ability = "Each night*, choose a player: they die. [You choose which players are which Minions. -? to +? Outsiders]"
        ),
        "legion" to Character(
            name = "Legion",
            type = CharType.DEMON,
            icon = R.drawable.icon_legion,
            ability = "Each night*, a player might die. Executions fail if only evil voted. You register as a Minion too. [Most players are Legion]"
        ),
        "leviathan" to Character(
            name = "Leviathan",
            type = CharType.DEMON,
            icon = R.drawable.icon_leviathan,
            ability = "If more than 1 good player is executed, evil wins. All players know you are in play. After day 5, evil wins."
        ),
        "lilmonsta" to Character(
            name = "Lil' Monsta",
            type = CharType.DEMON,
            icon = R.drawable.icon_lilmonsta,
            ability = "Each night, Minions choose who babysits Lil' Monsta & \"is the Demon\". Each night*, a player might die. [+1 Minion]"
        ),
        "lleech" to Character(
            name = "Lleech",
            type = CharType.DEMON,
            icon = R.drawable.icon_lleech,
            ability = "Each night*, choose a player: they die. You start by choosing a player: they are poisoned. You die if & only if they are dead."
        ),
        "lordoftyphon" to Character(
            name = "Lord of Typhon",
            type = CharType.DEMON,
            icon = R.drawable.icon_lordoftyphon,
            ability = "Each night*, choose a player: they die. [Evil characters are in a line. You are in the middle. +1 Minion. -? to +? Outsiders]"
        ),
        "nodashii" to Character(
            name = "No Dashii",
            type = CharType.DEMON,
            icon = R.drawable.icon_nodashii,
            ability = "Each night*, choose a player: they die. Your 2 Townsfolk neighbors are poisoned."
        ),
        "ojo" to Character(
            name = "Ojo",
            type = CharType.DEMON,
            icon = R.drawable.icon_ojo,
            ability = "Each night*, choose a character: they die. If they are not in play, the Storyteller chooses who dies."
        ),
        "po" to Character(
            name = "Po",
            type = CharType.DEMON,
            icon = R.drawable.icon_po,
            ability = "Each night*, you may choose a player: they die. If your last choice was no-one, choose 3 players tonight."
        ),
        "pukka" to Character(
            name = "Pukka",
            type = CharType.DEMON,
            icon = R.drawable.icon_pukka,
            ability = "Each night, choose a player: they are poisoned. The previously poisoned player dies then becomes healthy."
        ),
        "riot" to Character(
            name = "Riot",
            type = CharType.DEMON,
            icon = R.drawable.icon_riot,
            ability = "On day 3, Minions become Riot & nominees die but nominate an alive player immediately. This must happen."
        ),
        "shabaloth" to Character(
            name = "Shabaloth",
            type = CharType.DEMON,
            icon = R.drawable.icon_shabaloth,
            ability = "Each night*, choose 2 players: they die. A dead player you chose last night might be regurgitated."
        ),
        "vigormortis" to Character(
            name = "Vigormortis",
            type = CharType.DEMON,
            icon = R.drawable.icon_vigormortis,
            ability = "Each night*, choose a player: they die. Minions you kill keep their ability & poison 1 Townsfolk neighbor. [-1 Outsider]"
        ),
        "vortox" to Character(
            name = "Vortox",
            type = CharType.DEMON,
            icon = R.drawable.icon_vortox,
            ability = "Each night*, choose a player: they die. Townsfolk abilities yield false info. Each day, if no-one is executed, evil wins."
        ),
        "yaggababble" to Character(
            name = "Yaggababble",
            type = CharType.DEMON,
            icon = R.drawable.icon_yaggababble,
            ability = "You start knowing a secret phrase. For each time you said it publicly today, a player might die."
        ),
        "zombuul" to Character(
            name = "Zombuul",
            type = CharType.DEMON,
            icon = R.drawable.icon_zombuul,
            ability = "Each night*, if no-one died today, choose a player: they die. The 1st time you die, you live but register as dead."
        )
    )

    fun getCharacterInfo(id: String): Character? {
        val normalizedId = id.lowercase().replace(" ","").replace("-","").replace("_","")
        return characterData[normalizedId]
    }
}
