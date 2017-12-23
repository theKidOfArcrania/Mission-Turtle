# Mission Turtle

This is a top-down maze game involving a turtle as the main character of this game. This project was 
initially created as my end-of-year school project. 

## Overview
The game itself is simple: using the arrow or WASD keys, you would need to guide a turtle character 
to collect enough food for the level, then successfully exit in the water **portal**. Along the 
way the turtle will have to sucessfully avoid many obstacles and fellow creatures who all want 
to kill this turtle. 

## Libraries
 * This project utilizes [libtiled-java][22] (which is licensed under BSD) to convert TMX into the 
   MTP format used for storing level packs. This allows for easier editing (without spending more
   time to create a custom editor).

## Game Encyclopedia
The entries below will contain all the different standard maze elements. By extending the 
`Component` class or its subclasses, other custom maze elements can be created. 

### Obstacles
Anything that can stand in the way of **turtle**. Some obstacles are benevolent, others not so 
much.

#### ![Picture of Bucket][1] Bucket
Buckets are useful contraptions for carrying **water**. Once filled they can be used to 
extinguish **fire**. An empty or filled bucket can also deactivate **traps**. **Turtle** can 
maneuver these bucket onto any location as long as there are no impending obstacles in front, 
including another bucket. Note that both an empty and filled **bucket** would be destroyed once 
moved over **fire** (except that an empty **bucket** would not extinguish **fire**). 

#### ![Picture of Button][2] Button
This can trigger a **factory** when any creature or object moves over it. Usually this is 
color-coded to a corresponding **factory**, but it is not guaranteed.

#### ![Picture of Cannon][3] Cannon
These can shoot out cannon **projectiles** at various rates. They can be moved by **turtle** and
repositioned, just like buckets.  

#### ![Picture of Door][4] Door
These normally act like walls until **turtle** has the respectively-colored **key** to unlock 
the door. Once unlocked, the key used to unlock the door will be destroyed, so each key can only
 be used once.

#### ![Picture of Factory][5] Factory
This base can be used to clone any other object, when triggered by the respectively-colored 
**button**. These **factories** act like **walls** to other creatures.  (Note: by convention, 
the same colored factory to button would be paired up, but this might not always be the case, i.e.
if there are more factory-button mappings than there are colors.)

#### ![Picture of Fire][6] Fire
This fiery tile can mercilessly kill most other moving creatures, including **turtle**. The only
exception to this rule would be that a filled **bucket** would also extinguish the fire in the 
process, transforming it back into **sand**. 

#### ![Picture of Grass][7] Grass
This tall grass can block the view of any tiles under this grass. It could be anything under 
here, but it does not block the view of any other creatures. 

#### ![Picture of Portal][8] Portal
This is the exit-ticket in the level. However, you will have to collect enough food before 
exiting! If you try to go through the portal before consuming enough food, nothing will happen. 
Only **turtle** can go through this tile; anything else that tries to move across this portal 
will be stopped by some magical invisible force-field \[citation needed\]!

#### ![Picture of Sand][9] Sand
Sand. It's just regular-old sand, nothing else.

#### ![Picture of Sign][10] Sign
Come to hear it's tale! Any object/creature can walk over it but only (maybe?) the **turtle** 
has the wisdom to be able to read the sign.

#### ![Picture of Trap][11] Trap
It's a trap! If some unsuspecting creature or object moves over this trap, it *immediately* gets
destroyed to bits. At least the trap also gets destroyed. Watch out for these traps!

#### ![Picture of Wall][12] Wall
It's just a wall. Nothing can pass through this dead wall.

#### ![Picture of Water][13] Water
Water. Pretty much this is the **turtle** safe zone because all other creatures (with the 
exception of **Plastic**) will drown or die in water.

### Items
Useful stuff that can be used by **turtle** to solve the level.

#### ![Picture of Food][14] Food
Consisting of grain, seaweed, and some fish, this food is vital sustenance for **turtle**. 
**Turtle** must consume enough **food** in order to be able to go to the **portal** and proceed 
to the next level. 

#### ![Picture of Key][15] Key
This can be paired up with the respectively-colored **door** to unlock it.

### Creatures
Creatures are all the living moving objects including our **turtle**. Most creatures, however, 
are very ruthless and are out to kill **turtle**.

#### ![Picture of Bird][16] Bird
This feathered creature, although a dignified and apparently harmless creature, will in fact harm
**turtle**. It will try to fly as close to the turtle before gobbling it up, showing no mercy to
this turtle. It, however, cannot fly into the **water** nor into the **fire**.

#### ![Picture of Child][17] Child
These innocent looking "devils" can prove to be super deadly to **turtle**. Although they seen to
normally maneuver slowly, whenever they spot **turtle** down their line of sight, they will 
charge double speed (compared to the turtle) until they collide into some obstacle or **turtle**!
Watch out for these creatures. 

#### ![Picture of Lawnmower][18] Lawnmower
This is a machine that has gone astray. Cursed to move back and forth indefinitely (it will 
recoil back when it reaches a wall), it lost it's original purpose of mowing **grass**, but 
rather it could only mow and destroy **turtle** if the **turtle** gets in its path. It will 
surely short-circuit in **water** and it will be burned under **fire**.

#### ![Picture of Plastic Wrap][19] Plastic Wrap
This floaty devious creature can kill an unsuspecting **turtle** if the turtle crosses it's path
and consumes it. This plastic wrap will float in **water**, but once it reaches **sand**, it 
will be immobilized. 

#### ![Picture of Projectile][20] Projectile
These projectiles are created out of a **cannon**. This destroys any moving creatures along it's
path including other cannon balls. When it hits a  corner, it will bounce off in a clockwise 
direction (i.e. it will try  to turn right if it can). If it fails it will turn left if it can. 
Otherwise it will just recoil back.

#### ![Picture of Turtle][21] Turtle
It's you! You can control this character and guide the turtle through the game.

## License
Licensed under MIT.

[1]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Bucket.png
[2]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Button.png
[3]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Cannon.png
[4]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Door.png
[5]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Factory.png
[6]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Fire.png
[7]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Grass.png
[8]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Portal.png
[9]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Sand.png
[10]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Sign.png
[11]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Trap.png
[12]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Wall.png
[13]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Water.png
[14]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Food.png
[15]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Key.png
[16]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Bird.png
[17]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Child.png
[18]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Lawnmower.png
[19]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Plastic.png
[20]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Ball.png
[21]: https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Turtle.png
[22]: https://github.com/bjorn/tiled/tree/master/util/java/libtiled-java