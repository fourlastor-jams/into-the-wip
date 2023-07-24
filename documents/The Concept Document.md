# The Concept Document :sparkling_heart:
A high-level description of this game. What it's about and its feel. This is a 'living document', meaning it's subject to change along the way of the development cycle.

Other documents:
* [The System Design Document](/documents/systems/The%20System%20Design%20Document.md)
* [The Detailed Design Document](/documents/The%20Detailed%20Design%20Document.md)

## Index
* [High-Level Concepts](#high-level-concepts)
* [Product Description](#product-description)
    * [Player Experience](#player-experience)
    * [Aesthetics](#aesthetics)
    * [Monetization](#monetization)
    * [Technology, Tools and Platform](#technology-tools-and-platform)
    * [Development](#development)
* [Detailed Design](#detailed-design)
* [Other](#other)

## High-Level Concepts
High-level information about the game quickly and concisely.
* #### Write a short paragraph explaining the game:    
    It's a pokemon vs "Into the breach" game. Tactical, isometric grid combat with lots of systematic gameplay opportunities.
                         
* #### Describe the genre:
    Roguelike, Turn based strategy, Creature collection
    
* #### What is the target audience?
    The target players are non-casual gamers on Steam looking for a fun challenge, men and women aged 21-35.

    ##### Psychographics
    Primary motivators are Mastery-Achievement: completion, strategy and challenge, culminating in a motivation for gaining power. There is also a minor motivation to immerse oneself in the world design and discovery. 

    The game leans towards a cerebral-world motivation. Where it's fun to discover and think through your moves beforehand.

    ##### Demographics
    Men and women from age 21-35.

    ##### Technological/Environmental Context
    There will be a demo up on itch.
    Players will be using the game store page Steam to find and play the game.
    
* #### What other games are similar?
    Pokemon, "Into the Breach", Coromon, Wesnoth

* #### Unique Selling point
    ##### Why play this game?
    Want more of a tactical challenge than the average pokemon game. It also offers harder consequences like death and blood, etc than a pokemon game. The tactical challenge mechanics are similar to "Into the Breach". 
    The strategic challenge is roguelike-ish, maybe like "Slay the Spire" and "Faster than Light".

    ##### What makes the game fun?
    Progressing, proficiency/mastery, Roguelike procedural randomness, Unlocking new features / Achievements.

* #### What is the 'One Question'?
    The one question that can be used to resolve design questions is:
    > Is it collectable?
    
## Product Description
A Product-oriented description that describes an overview of the player experience in the game and the game systems that support the overall vision.

### Player Experience
#### Mechanics: the rules of the game world
* ##### What are the character's goals?        
    Defeat enemies, and capture other pokemons. Obtain abilities and powerups, items, etc. Gain experience and level up.

* ##### What abilities does the character have?
    The avatar:
    * move around the strategic map
    * interact with npc's and other exciting nodes
    
    The pokemons (on the strategic board):
    * move
    * attack
    * heal
    * support
    
* ##### What obstacles or difficulties will the character face?    
    Strategic map:
    * choosing your best strategic path to gain challenges and rewards
    
    Tactical board:
    * strategic missions
    * Other enemies
    * environmental hazards
    * capturing pokemons
    * getting items

* ##### What items can the character obtain
    Other pokemons, items like potions & stuff, abilities, gear, weapons
    
* ##### What resources must be managed?
    Pokemon health, collected abilities and items, a money currency, different tactical board options, time? space on the tactical board
    
#### Dynamics
The interaction between the player and the game mechanics
    
* ##### What type of proficiency will the player need to develop to become proficient at the game?
    It's a long-term cognitive strategic game. Thinking ahead in turns, and planning on how to be efficient in battle/combat. Being able to capture different pokemons on the strategic and/or tactical board, and knowing when to use them.

* ##### How does the player interact with the game at the software level?
    The player interacts with the keyboard & mouse, or controller.

### Aesthetics
The visual, audio, narrative, and psychological aspects of the game
* #### Describe the style and feel of the game
    Cyberpunk mechanimals. Gritty like 'Into the Breach', and still cutsy.
    Based on animals, instead mechs.

    Cute, animal, mechanical.

* #### Does the game use pixel art, line art, or realistic graphics?    
    Pixel art, isometric.

* #### What style of background music, ambient sounds will the game use?        
     TODO

* #### What is the relevant backstory for the game?    
    Something, collecting pokemons and a.. challenge
    A non-welcoming world. Why are there mechanical animals? where are they from? who made them? are they still being made? post-apocalyptic (adventure time?)

* #### What emotional state(s) does the game try to provoke?    
    Powerful & Brave(?), desperate(?)
    
### Monetization
* #### What are the plans for monetization?
    TODO: Possibly the game will be made available on the game store publication (Steam). Single price, paid once.

* #### What localizations will the game offer?
    English, Norwegian, Italian

* #### What are the plans for Marketing/PR?
    We will tweet about the game progression, possibly when we enter preproduction. Maybe devlogs? -On youtube, itch/github.

### Technology, Tools, and Platform
* #### What points in the development process are suitable for playtesting?    
    The main points for playtesting are when the basic game mechanics of the level screen are implemented, and when it is visualised. The questions that will be asked are:         
    * Is the gameplay and UI understandable?
    * Is the gameplay interesting?
    * How do the controls feel?
    * How is the pace of the game?
    * Are there any improvement suggestions?

* #### What is the project's scope?
    1 year. A demo in 2 months (by end of September 2023).

### Development
* #### What equipment is needed for this project? 
   A computer (with keyboard, mouse, and speakers) and internet access will be necessary to complete this project.

* #### What development tools will you use to develop this game?
    * LibGDX will be used to program the game. 
    * Aseprite and Gimp will be used to draw and create the visual art.

* #### Audio assets
    Audio assets will be obtained from third-party websites that make their assets available under the Creative Commons license for commercial purposes, and so the main task will be programming and creating some graphics.

* #### List the team members and their roles, responsibilities, and skills.    

    * Daniele Conti - programmer, design
    * Sandra Moen - artist, design

    This project will be completed in a team; audio will be obtained from third-party websites that make their assets available under the Creative Commons license for commercial purposes.

    Main task will be design, programming and creating graphics.

    TODO: Sign a contract about ownership and money?

* #### What are the tasks that need to be accomplished to create this game?        
    The three phases this project will go through will be:
    * Concept phase (1-3 months)
    * Preproduction phase (2-3 months)
    * Production phase
        * alpha
        * beta
        * release

    This project will use a simple Kanban board hosted on the project's GitHub page.
    The main sequence of steps to complete this project is as follows:    
    * Setting up a project scaffold
    * Programming game mechanics and UI
    * Creating and obtaining graphical assets
    * Obtaining audio assets
    * Controller support
    * Polishing
    * Publishing


## Detailed Design
### Game Design 
 * #### What are the core loops?
    * Progression loop: catching pokemons and unlockin content.
    * The strategical: Traversing the strategic board after each tactical battle.
    * The tactical turn based board of moving, atacking and using other abilities.

 * #### What are the player objectives and progression?
    The players objectives is to overcome every tactical and strategical challenges.
    The strategic map offers choices the player must choose between to gain better odds and power. On the strategic map the player must best all opponents with the boards limits and abilities available. The player progress by becoming more powerful, and being able to barter for the best abilities/choices available.

    * Immediate short-term: gain XP and money from a tactical fight
    * Immediate long-term: progress the strategic map to completion

 * #### Briefly describe the narrative and its elements
    

 * #### Briefly describe the main game systems
    Tactical combat based on abilities, and different pokemons. Merchant/NPC interactions to buy and sell ..things.

 * #### What are the primary forms of interactivity?
    * Desktop needs to have a functional mouse, keyboard, and screen. This game will not require a powerful computer. Controller support is wanted.
    * Android needs a basic Android device.

    ##### Player interactivity budget
    slow paced in the strategic board.
    medium paced inside the tactical board.
    Wanting to focus on short-term and long-term interactivity.

 * #### What gameplay data is displayed during the game?
    On the strategic board: 
    * locations
    * money/currency 
    * npcs 
    * health? 
    * list of pokemons

    On the tactical board: 
    * health 
    * the different available abilities 
    * turn counter 
    * objectives information
    * you own pokemons
    * enemies
    
 * #### What menus, screens, or overlays will there be?
    * splash screen
    * menu screen
    * options screen
    * overworld/strategic screen
    * tactical screen
    * pokemon list overlay, index (meta progression, how far along in the game you are)

## Other
### Questions to consider:
* Do you have to prepare before taking on the challenge?
* Can you prepare in different ways and still succeed?
* Does the environment in which the challenge takes place affect the challenge?
* Are there solid rules defined for the challenge you undertake?
* Can the core mechanic support multiple types of challenges?
* Can the player bring multiple abilities to bear on the challenge?
* At high levels of difficulty, does the player *have* to bring multiple abilities to bear on the challenge?
* Is there skill involved in using an ability? (If not, is this a fundamental "move" in the games, like moving one checker piece?)
* Are there multiple success states for overcoming the challenge? In other words, success should not have a single guaranteed result.


[Go back to the top](#the-concept-document-sparkling_heart)
