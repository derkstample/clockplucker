package clockplucker.data

//    Copyright 2026 Derek Rodriguez
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

import com.example.clockplucker.R

object CharacterRepository {
    private val characterData = mapOf(
        // TOWNSFOLK //
        "acrobat" to Character(
            id = "acrobat",
            name = TextValue.Resource(R.string.name_acrobat),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_acrobat,
            ability = TextValue.Resource(R.string.ability_acrobat)
        ),
        "alchemist" to Character(
            id = "alchemist",
            name = TextValue.Resource(R.string.name_alchemist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_alchemist,
            ability = TextValue.Resource(R.string.ability_alchemist)
        ),
        "alsaahir" to Character(
            id = "alsaahir",
            name = TextValue.Resource(R.string.name_alsaahir),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_alsaahir,
            ability = TextValue.Resource(R.string.ability_alsaahir)
        ),
        "amnesiac" to Character(
            id = "amnesiac",
            name = TextValue.Resource(R.string.name_amnesiac),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_amnesiac,
            ability = TextValue.Resource(R.string.ability_amnesiac)
        ),
        "artist" to Character(
            id = "artist",
            name = TextValue.Resource(R.string.name_artist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_artist,
            ability = TextValue.Resource(R.string.ability_artist)
        ),
        "atheist" to Character(
            id = "atheist",
            name = TextValue.Resource(R.string.name_atheist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_atheist,
            ability = TextValue.Resource(R.string.ability_atheist),
            overrideModifiers = listOf(CharType.MINION, CharType.DEMON)
        ),
        "balloonist" to Character(
            id = "balloonist",
            name = TextValue.Resource(R.string.name_balloonist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_balloonist,
            ability = TextValue.Resource(R.string.ability_balloonist),
            additiveModifiers = listOf(Count(), Count(townsfolk = -1, outsider = 1))
        ),
        "banshee" to Character(
            id = "banshee",
            name = TextValue.Resource(R.string.name_banshee),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_banshee,
            ability = TextValue.Resource(R.string.ability_banshee)
        ),
        "bountyhunter" to Character(
            id = "bountyhunter",
            name = TextValue.Resource(R.string.name_bountyhunter),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_bountyhunter,
            ability = TextValue.Resource(R.string.ability_bountyhunter)
        ),
        "cannibal" to Character(
            id = "cannibal",
            name = TextValue.Resource(R.string.name_cannibal),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_cannibal,
            ability = TextValue.Resource(R.string.ability_cannibal)
        ),
        "chambermaid" to Character(
            id = "chambermaid",
            name = TextValue.Resource(R.string.name_chambermaid),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_chambermaid,
            ability = TextValue.Resource(R.string.ability_chambermaid)
        ),
        "chef" to Character(
            id = "chef",
            name = TextValue.Resource(R.string.name_chef),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_chef,
            ability = TextValue.Resource(R.string.ability_chef)
        ),
        "choirboy" to Character(
            id = "choirboy",
            name = TextValue.Resource(R.string.name_choirboy),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_choirboy,
            dependsOn = "king",
            ability = TextValue.Resource(R.string.ability_choirboy)
        ),
        "clockmaker" to Character(
            id = "clockmaker",
            name = TextValue.Resource(R.string.name_clockmaker),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_clockmaker,
            ability = TextValue.Resource(R.string.ability_clockmaker)
        ),
        "courtier" to Character(
            id = "courtier",
            name = TextValue.Resource(R.string.name_courtier),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_courtier,
            ability = TextValue.Resource(R.string.ability_courtier)
        ),
        "cultleader" to Character(
            id = "cultleader",
            name = TextValue.Resource(R.string.name_cultleader),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_cultleader,
            ability = TextValue.Resource(R.string.ability_cultleader)
        ),
        "dreamer" to Character(
            id = "dreamer",
            name = TextValue.Resource(R.string.name_dreamer),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_dreamer,
            ability = TextValue.Resource(R.string.ability_dreamer)
        ),
        "empath" to Character(
            id = "empath",
            name = TextValue.Resource(R.string.name_empath),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_empath,
            ability = TextValue.Resource(R.string.ability_empath)
        ),
        "engineer" to Character(
            id = "engineer",
            name = TextValue.Resource(R.string.name_engineer),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_engineer,
            ability = TextValue.Resource(R.string.ability_engineer)
        ),
        "exorcist" to Character(
            id = "exorcist",
            name = TextValue.Resource(R.string.name_exorcist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_exorcist,
            ability = TextValue.Resource(R.string.ability_exorcist)
        ),
        "farmer" to Character(
            id = "farmer",
            name = TextValue.Resource(R.string.name_farmer),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_farmer,
            ability = TextValue.Resource(R.string.ability_farmer)
        ),
        "fisherman" to Character(
            id = "fisherman",
            name = TextValue.Resource(R.string.name_fisherman),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fisherman,
            ability = TextValue.Resource(R.string.ability_fisherman)
        ),
        "flowergirl" to Character(
            id = "flowergirl",
            name = TextValue.Resource(R.string.name_flowergirl),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_flowergirl,
            ability = TextValue.Resource(R.string.ability_flowergirl)
        ),
        "fool" to Character(
            id = "fool",
            name = TextValue.Resource(R.string.name_fool),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fool,
            ability = TextValue.Resource(R.string.ability_fool)
        ),
        "fortuneteller" to Character(
            id = "fortuneteller",
            name = TextValue.Resource(R.string.name_fortuneteller),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_fortuneteller,
            ability = TextValue.Resource(R.string.ability_fortuneteller)
        ),
        "gambler" to Character(
            id = "gambler",
            name = TextValue.Resource(R.string.name_gambler),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_gambler,
            ability = TextValue.Resource(R.string.ability_gambler)
        ),
        "general" to Character(
            id = "general",
            name = TextValue.Resource(R.string.name_general),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_general,
            ability = TextValue.Resource(R.string.ability_general)
        ),
        "gossip" to Character(
            id = "gossip",
            name = TextValue.Resource(R.string.name_gossip),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_gossip,
            ability = TextValue.Resource(R.string.ability_gossip)
        ),
        "grandmother" to Character(
            id = "grandmother",
            name = TextValue.Resource(R.string.name_grandmother),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_grandmother,
            ability = TextValue.Resource(R.string.ability_grandmother)
        ),
        "highpriestess" to Character(
            id = "highpriestess",
            name = TextValue.Resource(R.string.name_highpriestess),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_highpriestess,
            ability = TextValue.Resource(R.string.ability_highpriestess)
        ),
        "huntsman" to Character(
            id = "huntsman",
            name = TextValue.Resource(R.string.name_huntsman),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_huntsman,
            dependsOn = "damsel",
            ability = TextValue.Resource(R.string.ability_huntsman)
        ),
        "innkeeper" to Character(
            id = "innkeeper",
            name = TextValue.Resource(R.string.name_innkeeper),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_innkeeper,
            ability = TextValue.Resource(R.string.ability_innkeeper)
        ),
        "investigator" to Character(
            id = "investigator",
            name = TextValue.Resource(R.string.name_investigator),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_investigator,
            ability = TextValue.Resource(R.string.ability_investigator)
        ),
        "juggler" to Character(
            id = "juggler",
            name = TextValue.Resource(R.string.name_juggler),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_juggler,
            ability = TextValue.Resource(R.string.ability_juggler)
        ),
        "king" to Character(
            id = "king",
            name = TextValue.Resource(R.string.name_king),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_king,
            ability = TextValue.Resource(R.string.ability_king)
        ),
        "knight" to Character(
            id = "knight",
            name = TextValue.Resource(R.string.name_knight),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_knight,
            ability = TextValue.Resource(R.string.ability_knight)
        ),
        "librarian" to Character(
            id = "librarian",
            name = TextValue.Resource(R.string.name_librarian),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_librarian,
            ability = TextValue.Resource(R.string.ability_librarian)
        ),
        "lycanthrope" to Character(
            id = "lycanthrope",
            name = TextValue.Resource(R.string.name_lycanthrope),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_lycanthrope,
            ability = TextValue.Resource(R.string.ability_lycanthrope)
        ),
        "magician" to Character(
            id = "magician",
            name = TextValue.Resource(R.string.name_magician),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_magician,
            ability = TextValue.Resource(R.string.ability_magician)
        ),
        "mathematician" to Character(
            id = "mathematician",
            name = TextValue.Resource(R.string.name_mathematician),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_mathematician,
            ability = TextValue.Resource(R.string.ability_mathematician)
        ),
        "mayor" to Character(
            id = "mayor",
            name = TextValue.Resource(R.string.name_mayor),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_mayor,
            ability = TextValue.Resource(R.string.ability_mayor)
        ),
        "minstrel" to Character(
            id = "minstrel",
            name = TextValue.Resource(R.string.name_minstrel),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_minstrel,
            ability = TextValue.Resource(R.string.ability_minstrel)
        ),
        "monk" to Character(
            id = "monk",
            name = TextValue.Resource(R.string.name_monk),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_monk,
            ability = TextValue.Resource(R.string.ability_monk)
        ),
        "nightwatchman" to Character(
            id = "nightwatchman",
            name = TextValue.Resource(R.string.name_nightwatchman),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_nightwatchman,
            ability = TextValue.Resource(R.string.ability_nightwatchman)
        ),
        "noble" to Character(
            id = "noble",
            name = TextValue.Resource(R.string.name_noble),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_noble,
            ability = TextValue.Resource(R.string.ability_noble)
        ),
        "oracle" to Character(
            id = "oracle",
            name = TextValue.Resource(R.string.name_oracle),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_oracle,
            ability = TextValue.Resource(R.string.ability_oracle)
        ),
        "pacifist" to Character(
            id = "pacifist",
            name = TextValue.Resource(R.string.name_pacifist),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_pacifist,
            ability = TextValue.Resource(R.string.ability_pacifist)
        ),
        "philosopher" to Character(
            id = "philosopher",
            name = TextValue.Resource(R.string.name_philosopher),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_philosopher,
            ability = TextValue.Resource(R.string.ability_philosopher)
        ),
        "pixie" to Character(
            id = "pixie",
            name = TextValue.Resource(R.string.name_pixie),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_pixie,
            ability = TextValue.Resource(R.string.ability_pixie)
        ),
        "poppygrower" to Character(
            id = "poppygrower",
            name = TextValue.Resource(R.string.name_poppygrower),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_poppygrower,
            ability = TextValue.Resource(R.string.ability_poppygrower)
        ),
        "preacher" to Character(
            id = "preacher",
            name = TextValue.Resource(R.string.name_preacher),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_preacher,
            ability = TextValue.Resource(R.string.ability_preacher)
        ),
        "princess" to Character(
            id = "princess",
            name = TextValue.Resource(R.string.name_princess),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_princess,
            ability = TextValue.Resource(R.string.ability_princess)
        ),
        "professor" to Character(
            id = "professor",
            name = TextValue.Resource(R.string.name_professor),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_professor,
            ability = TextValue.Resource(R.string.ability_professor)
        ),
        "ravenkeeper" to Character(
            id = "ravenkeeper",
            name = TextValue.Resource(R.string.name_ravenkeeper),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_ravenkeeper,
            ability = TextValue.Resource(R.string.ability_ravenkeeper)
        ),
        "sage" to Character(
            id = "sage",
            name = TextValue.Resource(R.string.name_sage),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_sage,
            ability = TextValue.Resource(R.string.ability_sage)
        ),
        "sailor" to Character(
            id = "sailor",
            name = TextValue.Resource(R.string.name_sailor),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_sailor,
            ability = TextValue.Resource(R.string.ability_sailor)
        ),
        "savant" to Character(
            id = "savant",
            name = TextValue.Resource(R.string.name_savant),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_savant,
            ability = TextValue.Resource(R.string.ability_savant)
        ),
        "seamstress" to Character(
            id = "seamstress",
            name = TextValue.Resource(R.string.name_seamstress),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_seamstress,
            ability = TextValue.Resource(R.string.ability_seamstress)
        ),
        "shugenja" to Character(
            id = "shugenja",
            name = TextValue.Resource(R.string.name_shugenja),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_shugenja,
            ability = TextValue.Resource(R.string.ability_shugenja)
        ),
        "slayer" to Character(
            id = "slayer",
            name = TextValue.Resource(R.string.name_slayer),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_slayer,
            ability = TextValue.Resource(R.string.ability_slayer)
        ),
        "snakecharmer" to Character(
            id = "snakecharmer",
            name = TextValue.Resource(R.string.name_snakecharmer),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_snakecharmer,
            ability = TextValue.Resource(R.string.ability_snakecharmer)
        ),
        "soldier" to Character(
            id = "soldier",
            name = TextValue.Resource(R.string.name_soldier),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_soldier,
            ability = TextValue.Resource(R.string.ability_soldier)
        ),
        "steward" to Character(
            id = "steward",
            name = TextValue.Resource(R.string.name_steward),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_steward,
            ability = TextValue.Resource(R.string.ability_steward)
        ),
        "tealady" to Character(
            id = "tealady",
            name = TextValue.Resource(R.string.name_tealady),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_tealady,
            ability = TextValue.Resource(R.string.ability_tealady)
        ),
        "towncrier" to Character(
            id = "towncrier",
            name = TextValue.Resource(R.string.name_towncrier),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_towncrier,
            ability = TextValue.Resource(R.string.ability_towncrier)
        ),
        "undertaker" to Character(
            id = "undertaker",
            name = TextValue.Resource(R.string.name_undertaker),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_undertaker,
            ability = TextValue.Resource(R.string.ability_undertaker)
        ),
        "villageidiot" to Character(
            id = "villageidiot",
            name = TextValue.Resource(R.string.name_villageidiot),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_villageidiot,
            maxInstances = 3,
            ability = TextValue.Resource(R.string.ability_villageidiot)
        ),
        "virgin" to Character(
            id = "virgin",
            name = TextValue.Resource(R.string.name_virgin),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_virgin,
            ability = TextValue.Resource(R.string.ability_virgin)
        ),
        "washerwoman" to Character(
            id = "washerwoman",
            name = TextValue.Resource(R.string.name_washerwoman),
            type = CharType.TOWNSFOLK,
            icon = R.drawable.icon_washerwoman,
            ability = TextValue.Resource(R.string.ability_washerwoman)
        ),
        // OUTSIDERS //
        "barber" to Character(
            id = "barber",
            name = TextValue.Resource(R.string.name_barber),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_barber,
            ability = TextValue.Resource(R.string.ability_barber)
        ),
        "butler" to Character(
            id = "butler",
            name = TextValue.Resource(R.string.name_butler),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_butler,
            ability = TextValue.Resource(R.string.ability_butler)
        ),
        "damsel" to Character(
            id = "damsel",
            name = TextValue.Resource(R.string.name_damsel),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_damsel,
            ability = TextValue.Resource(R.string.ability_damsel)
        ),
        "drunk" to Character(
            id = "drunk",
            name = TextValue.Resource(R.string.name_drunk),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_drunk,
            ability = TextValue.Resource(R.string.ability_drunk),
            thinksTheyAre = listOf(CharType.TOWNSFOLK)
        ),
        "golem" to Character(
            id = "golem",
            name = TextValue.Resource(R.string.name_golem),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_golem,
            ability = TextValue.Resource(R.string.ability_golem)
        ),
        "goon" to Character(
            id = "goon",
            name = TextValue.Resource(R.string.name_goon),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_goon,
            ability = TextValue.Resource(R.string.ability_goon)
        ),
        "hatter" to Character(
            id = "hatter",
            name = TextValue.Resource(R.string.name_hatter),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_hatter,
            ability = TextValue.Resource(R.string.ability_hatter)
        ),
        "heretic" to Character(
            id = "heretic",
            name = TextValue.Resource(R.string.name_heretic),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_heretic,
            hardJinxedWith = listOf("baron","godfather","lleech","pithag","spy","widow"),
            ability = TextValue.Resource(R.string.ability_heretic)
        ),
        "hermit" to Character(
            id = "hermit",
            name = TextValue.Resource(R.string.name_hermit),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_hermit,
            ability = TextValue.Resource(R.string.ability_hermit),
            additiveModifiers = listOf(Count(), Count(townsfolk = 1, outsider = -1))
        ),
        "klutz" to Character(
            id = "klutz",
            name = TextValue.Resource(R.string.name_klutz),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_klutz,
            ability = TextValue.Resource(R.string.ability_klutz)
        ),
        "lunatic" to Character(
            id = "lunatic",
            name = TextValue.Resource(R.string.name_lunatic),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_lunatic,
            ability = TextValue.Resource(R.string.ability_lunatic),
            thinksTheyAre = listOf(CharType.DEMON)
        ),
        "moonchild" to Character(
            id = "moonchild",
            name = TextValue.Resource(R.string.name_moonchild),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_moonchild,
            ability = TextValue.Resource(R.string.ability_moonchild)
        ),
        "mutant" to Character(
            id = "mutant",
            name = TextValue.Resource(R.string.name_mutant),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_mutant,
            ability = TextValue.Resource(R.string.ability_mutant)
        ),
        "ogre" to Character(
            id = "ogre",
            name = TextValue.Resource(R.string.name_ogre),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_ogre,
            ability = TextValue.Resource(R.string.ability_ogre)
        ),
        "plaguedoctor" to Character(
            id = "plaguedoctor",
            name = TextValue.Resource(R.string.name_plaguedoctor),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_plaguedoctor,
            ability = TextValue.Resource(R.string.ability_plaguedoctor)
        ),
        "politician" to Character(
            id = "politician",
            name = TextValue.Resource(R.string.name_politician),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_politician,
            ability = TextValue.Resource(R.string.ability_politician)
        ),
        "puzzlemaster" to Character(
            id = "puzzlemaster",
            name = TextValue.Resource(R.string.name_puzzlemaster),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_puzzlemaster,
            ability = TextValue.Resource(R.string.ability_puzzlemaster)
        ),
        "recluse" to Character(
            id = "recluse",
            name = TextValue.Resource(R.string.name_recluse),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_recluse,
            ability = TextValue.Resource(R.string.ability_recluse)
        ),
        "saint" to Character(
            id = "saint",
            name = TextValue.Resource(R.string.name_saint),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_saint,
            ability = TextValue.Resource(R.string.ability_saint)
        ),
        "snitch" to Character(
            id = "snitch",
            name = TextValue.Resource(R.string.name_snitch),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_snitch,
            ability = TextValue.Resource(R.string.ability_snitch)
        ),
        "sweetheart" to Character(
            id = "sweetheart",
            name = TextValue.Resource(R.string.name_sweetheart),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_sweetheart,
            ability = TextValue.Resource(R.string.ability_sweetheart)
        ),
        "tinker" to Character(
            id = "tinker",
            name = TextValue.Resource(R.string.name_tinker),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_tinker,
            ability = TextValue.Resource(R.string.ability_tinker)
        ),
        "zealot" to Character(
            id = "zealot",
            name = TextValue.Resource(R.string.name_zealot),
            type = CharType.OUTSIDER,
            icon = R.drawable.icon_zealot,
            ability = TextValue.Resource(R.string.ability_zealot)
        ),
        // MINIONS //
        "assassin" to Character(
            id = "assassin",
            name = TextValue.Resource(R.string.name_assassin),
            type = CharType.MINION,
            icon = R.drawable.icon_assassin,
            ability = TextValue.Resource(R.string.ability_assassin)
        ),
        "baron" to Character(
            id = "baron",
            name = TextValue.Resource(R.string.name_baron),
            type = CharType.MINION,
            icon = R.drawable.icon_baron,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_baron),
            additiveModifiers = listOf(Count(townsfolk = -2, outsider = 2))
        ),
        "boffin" to Character(
            id = "boffin",
            name = TextValue.Resource(R.string.name_boffin),
            type = CharType.MINION,
            icon = R.drawable.icon_boffin,
            ability = TextValue.Resource(R.string.ability_boffin)
        ),
        "boomdandy" to Character(
            id = "boomdandy",
            name = TextValue.Resource(R.string.name_boomdandy),
            type = CharType.MINION,
            icon = R.drawable.icon_boomdandy,
            ability = TextValue.Resource(R.string.ability_boomdandy)
        ),
        "cerenovus" to Character(
            id = "cerenovus",
            name = TextValue.Resource(R.string.name_cerenovus),
            type = CharType.MINION,
            icon = R.drawable.icon_cerenovus,
            ability = TextValue.Resource(R.string.ability_cerenovus)
        ),
        "devilsadvocate" to Character(
            id = "devilsadvocate",
            name = TextValue.Resource(R.string.name_devilsadvocate),
            type = CharType.MINION,
            icon = R.drawable.icon_devilsadvocate,
            ability = TextValue.Resource(R.string.ability_devilsadvocate)
        ),
        "eviltwin" to Character(
            id = "eviltwin",
            name = TextValue.Resource(R.string.name_eviltwin),
            type = CharType.MINION,
            icon = R.drawable.icon_eviltwin,
            ability = TextValue.Resource(R.string.ability_eviltwin)
        ),
        "fearmonger" to Character(
            id = "fearmonger",
            name = TextValue.Resource(R.string.name_fearmonger),
            type = CharType.MINION,
            icon = R.drawable.icon_fearmonger,
            ability = TextValue.Resource(R.string.ability_fearmonger)
        ),
        "goblin" to Character(
            id = "goblin",
            name = TextValue.Resource(R.string.name_goblin),
            type = CharType.MINION,
            icon = R.drawable.icon_goblin,
            ability = TextValue.Resource(R.string.ability_goblin)
        ),
        "godfather" to Character(
            id = "godfather",
            name = TextValue.Resource(R.string.name_godfather),
            type = CharType.MINION,
            icon = R.drawable.icon_godfather,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_godfather),
            additiveModifiers = listOf(Count(townsfolk = -1, outsider = 1), Count(townsfolk = 1, outsider = -1))
        ),
        "harpy" to Character(
            id = "harpy",
            name = TextValue.Resource(R.string.name_harpy),
            type = CharType.MINION,
            icon = R.drawable.icon_harpy,
            ability = TextValue.Resource(R.string.ability_harpy)
        ),
        "marionette" to Character(
            id = "marionette",
            name = TextValue.Resource(R.string.name_marionette),
            type = CharType.MINION,
            icon = R.drawable.icon_marionette,
            ability = TextValue.Resource(R.string.ability_marionette),
            thinksTheyAre = listOf(CharType.TOWNSFOLK, CharType.OUTSIDER)
        ),
        "mastermind" to Character(
            id = "mastermind",
            name = TextValue.Resource(R.string.name_mastermind),
            type = CharType.MINION,
            icon = R.drawable.icon_mastermind,
            ability = TextValue.Resource(R.string.ability_mastermind)
        ),
        "mezepheles" to Character(
            id = "mezepheles",
            name = TextValue.Resource(R.string.name_mezepheles),
            type = CharType.MINION,
            icon = R.drawable.icon_mezepheles,
            ability = TextValue.Resource(R.string.ability_mezepheles)
        ),
        "organgrinder" to Character(
            id = "organgrinder",
            name = TextValue.Resource(R.string.name_organgrinder),
            type = CharType.MINION,
            icon = R.drawable.icon_organgrinder,
            ability = TextValue.Resource(R.string.ability_organgrinder)
        ),
        "pithag" to Character(
            id = "pithag",
            name = TextValue.Resource(R.string.name_pithag),
            type = CharType.MINION,
            icon = R.drawable.icon_pithag,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_pithag)
        ),
        "poisoner" to Character(
            id = "poisoner",
            name = TextValue.Resource(R.string.name_poisoner),
            type = CharType.MINION,
            icon = R.drawable.icon_poisoner,
            ability = TextValue.Resource(R.string.ability_poisoner)
        ),
        "psychopath" to Character(
            id = "psychopath",
            name = TextValue.Resource(R.string.name_psychopath),
            type = CharType.MINION,
            icon = R.drawable.icon_psychopath,
            ability = TextValue.Resource(R.string.ability_psychopath)
        ),
        "scarletwoman" to Character(
            id = "scarletwoman",
            name = TextValue.Resource(R.string.name_scarletwoman),
            type = CharType.MINION,
            icon = R.drawable.icon_scarletwoman,
            ability = TextValue.Resource(R.string.ability_scarletwoman)
        ),
        "spy" to Character(
            id = "spy",
            name = TextValue.Resource(R.string.name_spy),
            type = CharType.MINION,
            icon = R.drawable.icon_spy,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_spy)
        ),
        "summoner" to Character(
            id = "summoner",
            name = TextValue.Resource(R.string.name_summoner),
            type = CharType.MINION,
            icon = R.drawable.icon_summoner,
            ability = TextValue.Resource(R.string.ability_summoner),
            additiveModifiers = listOf(Count(townsfolk = 1, demon = -1))
        ),
        "vizier" to Character(
            id = "vizier",
            name = TextValue.Resource(R.string.name_vizier),
            type = CharType.MINION,
            icon = R.drawable.icon_vizier,
            ability = TextValue.Resource(R.string.ability_vizier)
        ),
        "widow" to Character(
            id = "widow",
            name = TextValue.Resource(R.string.name_widow),
            type = CharType.MINION,
            icon = R.drawable.icon_widow,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_widow)
        ),
        "witch" to Character(
            id = "witch",
            name = TextValue.Resource(R.string.name_witch),
            type = CharType.MINION,
            icon = R.drawable.icon_witch,
            ability = TextValue.Resource(R.string.ability_witch)
        ),
        "wizard" to Character(
            id = "wizard",
            name = TextValue.Resource(R.string.name_wizard),
            type = CharType.MINION,
            icon = R.drawable.icon_wizard,
            ability = TextValue.Resource(R.string.ability_wizard)
        ),
        "wraith" to Character(
            id = "wraith",
            name = TextValue.Resource(R.string.name_wraith),
            type = CharType.MINION,
            icon = R.drawable.icon_wraith,
            ability = TextValue.Resource(R.string.ability_wraith)
        ),
        "xaan" to Character(
            id = "xaan",
            name = TextValue.Resource(R.string.name_xaan),
            type = CharType.MINION,
            icon = R.drawable.icon_xaan,
            ability = TextValue.Resource(R.string.ability_xaan),
            additiveModifiers = listOf(
                Count(townsfolk = 4, outsider = -4),
                Count(townsfolk = 3, outsider = -3),
                Count(townsfolk = 2, outsider = -2),
                Count(townsfolk = 1, outsider = -1),
                Count(),
                Count(townsfolk = -1, outsider = 1),
                Count(townsfolk = -2, outsider = 2),
                Count(townsfolk = -3, outsider = 3),
                Count(townsfolk = -4, outsider = 4)
            )
        ),
        // DEMONS //
        "alhadikhia" to Character(
            id = "alhadikhia",
            name = TextValue.Resource(R.string.name_alhadikhia),
            type = CharType.DEMON,
            icon = R.drawable.icon_alhadikhia,
            ability = TextValue.Resource(R.string.ability_alhadikhia)
        ),
        "fanggu" to Character(
            id = "fanggu",
            name = TextValue.Resource(R.string.name_fanggu),
            type = CharType.DEMON,
            icon = R.drawable.icon_fanggu,
            ability = TextValue.Resource(R.string.ability_fanggu),
            additiveModifiers = listOf(Count(townsfolk = -1, outsider = 1))
        ),
        "imp" to Character(
            id = "imp",
            name = TextValue.Resource(R.string.name_imp),
            type = CharType.DEMON,
            icon = R.drawable.icon_imp,
            ability = TextValue.Resource(R.string.ability_imp)
        ),
        "kazali" to Character(
            id = "kazali",
            name = TextValue.Resource(R.string.name_kazali),
            type = CharType.DEMON,
            icon = R.drawable.icon_kazali,
            ability = TextValue.Resource(R.string.ability_kazali),
            overrideModifiers = listOf(CharType.MINION)
        ),
        "legion" to Character(
            id = "legion",
            name = TextValue.Resource(R.string.name_legion),
            type = CharType.DEMON,
            icon = R.drawable.icon_legion,
            maxInstances = 15,
            ability = TextValue.Resource(R.string.ability_legion),
            overrideModifiers = listOf(CharType.MINION)
        ),
        "leviathan" to Character(
            id = "leviathan",
            name = TextValue.Resource(R.string.name_leviathan),
            type = CharType.DEMON,
            icon = R.drawable.icon_leviathan,
            ability = TextValue.Resource(R.string.ability_leviathan)
        ),
        "lilmonsta" to Character(
            id = "lilmonsta",
            name = TextValue.Resource(R.string.name_lilmonsta),
            type = CharType.DEMON,
            icon = R.drawable.icon_lilmonsta,
            ability = TextValue.Resource(R.string.ability_lilmonsta),
            additiveModifiers = listOf(Count(minion = 1, demon = -1))
        ),
        "lleech" to Character(
            id = "lleech",
            name = TextValue.Resource(R.string.name_lleech),
            type = CharType.DEMON,
            icon = R.drawable.icon_lleech,
            hardJinxedWith = listOf("heretic"),
            ability = TextValue.Resource(R.string.ability_lleech)
        ),
        "lordoftyphon" to Character(
            id = "lordoftyphon",
            name = TextValue.Resource(R.string.name_lordoftyphon),
            type = CharType.DEMON,
            icon = R.drawable.icon_lordoftyphon,
            ability = TextValue.Resource(R.string.ability_lordoftyphon),
            overrideModifiers = listOf(CharType.MINION)
        ),
        "nodashii" to Character(
            id = "nodashii",
            name = TextValue.Resource(R.string.name_nodashii),
            type = CharType.DEMON,
            icon = R.drawable.icon_nodashii,
            ability = TextValue.Resource(R.string.ability_nodashii)
        ),
        "ojo" to Character(
            id = "ojo",
            name = TextValue.Resource(R.string.name_ojo),
            type = CharType.DEMON,
            icon = R.drawable.icon_ojo,
            ability = TextValue.Resource(R.string.ability_ojo)
        ),
        "po" to Character(
            id = "po",
            name = TextValue.Resource(R.string.name_po),
            type = CharType.DEMON,
            icon = R.drawable.icon_po,
            ability = TextValue.Resource(R.string.ability_po)
        ),
        "pukka" to Character(
            id = "pukka",
            name = TextValue.Resource(R.string.name_pukka),
            type = CharType.DEMON,
            icon = R.drawable.icon_pukka,
            ability = TextValue.Resource(R.string.ability_pukka)
        ),
        "riot" to Character(
            id = "riot",
            name = TextValue.Resource(R.string.name_riot),
            type = CharType.DEMON,
            icon = R.drawable.icon_riot,
            ability = TextValue.Resource(R.string.ability_riot)
        ),
        "shabaloth" to Character(
            id = "shabaloth",
            name = TextValue.Resource(R.string.name_shabaloth),
            type = CharType.DEMON,
            icon = R.drawable.icon_shabaloth,
            ability = TextValue.Resource(R.string.ability_shabaloth)
        ),
        "vigormortis" to Character(
            id = "vigormortis",
            name = TextValue.Resource(R.string.name_vigormortis),
            type = CharType.DEMON,
            icon = R.drawable.icon_vigormortis,
            ability = TextValue.Resource(R.string.ability_vigormortis),
            additiveModifiers = listOf(Count(townsfolk = 1, outsider = -1))
        ),
        "vortox" to Character(
            id = "vortox",
            name = TextValue.Resource(R.string.name_vortox),
            type = CharType.DEMON,
            icon = R.drawable.icon_vortox,
            ability = TextValue.Resource(R.string.ability_vortox)
        ),
        "yaggababble" to Character(
            id = "yaggababble",
            name = TextValue.Resource(R.string.name_yaggababble),
            type = CharType.DEMON,
            icon = R.drawable.icon_yaggababble,
            ability = TextValue.Resource(R.string.ability_yaggababble)
        ),
        "zombuul" to Character(
            id = "zombuul",
            name = TextValue.Resource(R.string.name_zombuul),
            type = CharType.DEMON,
            icon = R.drawable.icon_zombuul,
            ability = TextValue.Resource(R.string.ability_zombuul)
        ),
        // FABLED //
        "angel" to Character(
            id = "angel",
            name = TextValue.Resource(R.string.name_angel),
            type = CharType.FABLED,
            icon = R.drawable.icon_angel,
            ability = TextValue.Resource(R.string.ability_angel)
        ),
        "buddhist" to Character(
            id = "buddhist",
            name = TextValue.Resource(R.string.name_buddhist),
            type = CharType.FABLED,
            icon = R.drawable.icon_buddhist,
            ability = TextValue.Resource(R.string.ability_buddhist)
        ),
        "deusexfiasco" to Character(
            id = "deusexfiasco",
            name = TextValue.Resource(R.string.name_deusexfiasco),
            type = CharType.FABLED,
            icon = R.drawable.icon_deusexfiasco,
            ability = TextValue.Resource(R.string.ability_deusexfiasco)
        ),
        "djinn" to Character(
            id = "djinn",
            name = TextValue.Resource(R.string.name_djinn),
            type = CharType.FABLED,
            icon = R.drawable.icon_djinn,
            ability = TextValue.Resource(R.string.ability_djinn)
        ),
        "doomsayer" to Character(
            id = "doomsayer",
            name = TextValue.Resource(R.string.name_doomsayer),
            type = CharType.FABLED,
            icon = R.drawable.icon_doomsayer,
            ability = TextValue.Resource(R.string.ability_doomsayer)
        ),
        "duchess" to Character(
            id = "duchess",
            name = TextValue.Resource(R.string.name_duchess),
            type = CharType.FABLED,
            icon = R.drawable.icon_duchess,
            ability = TextValue.Resource(R.string.ability_duchess)
        ),
        "ferryman" to Character(
            id = "ferryman",
            name = TextValue.Resource(R.string.name_ferryman),
            type = CharType.FABLED,
            icon = R.drawable.icon_ferryman,
            ability = TextValue.Resource(R.string.ability_ferryman)
        ),
        "fibbin" to Character(
            id = "fibbin",
            name = TextValue.Resource(R.string.name_fibbin),
            type = CharType.FABLED,
            icon = R.drawable.icon_fibbin,
            ability = TextValue.Resource(R.string.ability_fibbin)
        ),
        "fiddler" to Character(
            id = "fiddler",
            name = TextValue.Resource(R.string.name_fiddler),
            type = CharType.FABLED,
            icon = R.drawable.icon_fiddler,
            ability = TextValue.Resource(R.string.ability_fiddler)
        ),
        "hellslibrarian" to Character(
            id = "hellslibrarian",
            name = TextValue.Resource(R.string.name_hellslibrarian),
            type = CharType.FABLED,
            icon = R.drawable.icon_hellslibrarian,
            ability = TextValue.Resource(R.string.ability_hellslibrarian)
        ),
        "revolutionary" to Character(
            id = "revolutionary",
            name = TextValue.Resource(R.string.name_revolutionary),
            type = CharType.FABLED,
            icon = R.drawable.icon_revolutionary,
            ability = TextValue.Resource(R.string.ability_revolutionary)
        ),
        "sentinel" to Character(
            id = "sentinel",
            name = TextValue.Resource(R.string.name_sentinel),
            type = CharType.FABLED,
            icon = R.drawable.icon_sentinel,
            ability = TextValue.Resource(R.string.ability_sentinel)
        ),
        "spiritofivory" to Character(
            id = "spiritofivory",
            name = TextValue.Resource(R.string.name_spiritofivory),
            type = CharType.FABLED,
            icon = R.drawable.icon_spiritofivory,
            ability = TextValue.Resource(R.string.ability_spiritofivory)
        ),
        "toymaker" to Character(
            id = "toymaker",
            name = TextValue.Resource(R.string.name_toymaker),
            type = CharType.FABLED,
            icon = R.drawable.icon_toymaker,
            ability = TextValue.Resource(R.string.ability_toymaker)
        ),
        // LORIC //
        "bigwig" to Character(
            id = "bigwig",
            name = TextValue.Resource(R.string.name_bigwig),
            type = CharType.LORIC,
            icon = R.drawable.icon_big_wig,
            ability = TextValue.Resource(R.string.ability_bigwig)
        ),
        "bootlegger" to Character(
            id = "bootlegger",
            name = TextValue.Resource(R.string.name_bootlegger),
            type = CharType.LORIC,
            icon = R.drawable.icon_bootlegger,
            ability = TextValue.Resource(R.string.ability_bootlegger)
        ),
        "gardener" to Character(
            id = "gardener",
            name = TextValue.Resource(R.string.name_gardener),
            type = CharType.LORIC,
            icon = R.drawable.icon_gardener,
            ability = TextValue.Resource(R.string.ability_gardener)
        ),
        "godofug" to Character(
            id = "godofug",
            name = TextValue.Resource(R.string.name_godofug),
            type = CharType.LORIC,
            icon = R.drawable.icon_godofug,
            ability = TextValue.Resource(R.string.ability_godofug)
        ),
        "hindu" to Character(
            id = "hindu",
            name = TextValue.Resource(R.string.name_hindu),
            type = CharType.LORIC,
            icon = R.drawable.icon_hindu,
            ability = TextValue.Resource(R.string.ability_hindu)
        ),
        "knaves" to Character(
            id = "knaves",
            name = TextValue.Resource(R.string.name_knaves),
            type = CharType.LORIC,
            icon = R.drawable.icon_knaves,
            ability = TextValue.Resource(R.string.ability_knaves)
        ),
        "pope" to Character(
            id = "pope",
            name = TextValue.Resource(R.string.name_pope),
            type = CharType.LORIC,
            icon = R.drawable.icon_pope,
            ability = TextValue.Resource(R.string.ability_pope)
        ),
        "stormcatcher" to Character(
            id = "stormcatcher",
            name = TextValue.Resource(R.string.name_stormcatcher),
            type = CharType.LORIC,
            icon = R.drawable.icon_stormcatcher,
            ability = TextValue.Resource(R.string.ability_stormcatcher)
        ),
        "tor" to Character(
            id = "tor",
            name = TextValue.Resource(R.string.name_tor),
            type = CharType.LORIC,
            icon = R.drawable.icon_tor,
            ability = TextValue.Resource(R.string.ability_tor)
        ),
        "ventriloquist" to Character(
            id = "ventriloquist",
            name = TextValue.Resource(R.string.name_ventriloquist),
            type = CharType.LORIC,
            icon = R.drawable.icon_ventriloquist,
            ability = TextValue.Resource(R.string.ability_ventriloquist)
        ),
        "zenomancer" to Character(
            id = "zenomancer",
            name = TextValue.Resource(R.string.name_zenomancer),
            type = CharType.LORIC,
            icon = R.drawable.icon_zenomancer,
            ability = TextValue.Resource(R.string.ability_zenomancer)
        ),
        // TRAVELLERS //
        "apprentice" to Character(
            id = "apprentice",
            name = TextValue.Resource(R.string.name_apprentice),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_apprentice,
            ability = TextValue.Resource(R.string.ability_apprentice)
        ),
        "barista" to Character(
            id = "barista",
            name = TextValue.Resource(R.string.name_barista),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_barista,
            ability = TextValue.Resource(R.string.ability_barista)
        ),
        "beggar" to Character(
            id = "beggar",
            name = TextValue.Resource(R.string.name_beggar),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_beggar,
            ability = TextValue.Resource(R.string.ability_beggar)
        ),
        "bishop" to Character(
            id = "bishop",
            name = TextValue.Resource(R.string.name_bishop),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_bishop,
            ability = TextValue.Resource(R.string.ability_bishop)
        ),
        "bonecollector" to Character(
            id = "bonecollector",
            name = TextValue.Resource(R.string.name_bonecollector),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_bonecollector,
            ability = TextValue.Resource(R.string.ability_bonecollector)
        ),
        "bureaucrat" to Character(
            id = "bureaucrat",
            name = TextValue.Resource(R.string.name_bureaucrat),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_bureaucrat,
            ability = TextValue.Resource(R.string.ability_bureaucrat)
        ),
        "butcher" to Character(
            id = "butcher",
            name = TextValue.Resource(R.string.name_butcher),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_butcher,
            ability = TextValue.Resource(R.string.ability_butcher)
        ),
        "cacklejack" to Character(
            id = "cacklejack",
            name = TextValue.Resource(R.string.name_cacklejack),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_cacklejack,
            ability = TextValue.Resource(R.string.ability_cacklejack)
        ),
        "deviant" to Character(
            id = "deviant",
            name = TextValue.Resource(R.string.name_deviant),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_deviant,
            ability = TextValue.Resource(R.string.ability_deviant)
        ),
        "gangster" to Character(
            id = "gangster",
            name = TextValue.Resource(R.string.name_gangster),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_gangster,
            ability = TextValue.Resource(R.string.ability_gangster)
        ),
        "gnome" to Character(
            id = "gnome",
            name = TextValue.Resource(R.string.name_gnome),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_gnome,
            ability = TextValue.Resource(R.string.ability_gnome)
        ),
        "gunslinger" to Character(
            id = "gunslinger",
            name = TextValue.Resource(R.string.name_gunslinger),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_gunslinger,
            ability = TextValue.Resource(R.string.ability_gunslinger)
        ),
        "harlot" to Character(
            id = "harlot",
            name = TextValue.Resource(R.string.name_harlot),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_harlot,
            ability = TextValue.Resource(R.string.ability_harlot)
        ),
        "judge" to Character(
            id = "judge",
            name = TextValue.Resource(R.string.name_judge),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_judge,
            ability = TextValue.Resource(R.string.ability_judge)
        ),
        "matron" to Character(
            id = "matron",
            name = TextValue.Resource(R.string.name_matron),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_matron,
            ability = TextValue.Resource(R.string.ability_matron)
        ),
        "scapegoat" to Character(
            id = "scapegoat",
            name = TextValue.Resource(R.string.name_scapegoat),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_scapegoat,
            ability = TextValue.Resource(R.string.ability_scapegoat)
        ),
        "thief" to Character(
            id = "thief",
            name = TextValue.Resource(R.string.name_thief),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_thief,
            ability = TextValue.Resource(R.string.ability_thief)
        ),
        "voudon" to Character(
            id = "voudon",
            name = TextValue.Resource(R.string.name_voudon),
            type = CharType.TRAVELLER,
            icon = R.drawable.icon_voudon,
            ability = TextValue.Resource(R.string.ability_voudon)
        )
    )

    fun getCharacterInfo(id: String): Character? {
        val normalizedId = id.lowercase().replace(" ","").replace("-","").replace("_","")
        return characterData[normalizedId]
    }
}