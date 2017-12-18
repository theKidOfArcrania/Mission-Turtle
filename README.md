# Mission Turtle

This is a top-down maze game involving a turtle as the main character of this game. This project was initially 
created as my end-of-year school project. 

## Overview
The game itself is simple: using the arrow or WASD keys, you would need to guide a turtle character to collect enough
food for the level, then successfully exit in the water **portal**. Along the way the turtle will have to 
sucessfully avoid many obstacles and fellow creatures who all want to kill this turtle. 

## Game Encyclopedia
The entries below will contain all the different standard maze components. These are not the only components that can
used in a level. In fact, this can be extended to implement other components. 

### Obstacles
Anything that can stand in the way of **turtle**. Some obstacles are benevolent, others not so much.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Bucket.png) Bucket
Buckets are useful contraptions for carrying **water**. Once filled they can be used to extinguish **fire**. An empty
or filled bucket can also deactivate **traps**. **Turtle** can maneuver these bucket onto any location as long as 
there are no impending obstacles in front, including another bucket. Note that both an empty and filled **bucket** 
would be destroyed once moved over **fire** (except that an empty **bucket** would not extinguish **fire**). 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Button.png) Button
This can trigger a **factory** when any creature or object moves over it. Usually this is color-coded to a 
corresponding **factory**, but it is not guaranteed.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Cannon.png) Cannon
These can shoot out cannon **projectiles** at various rates. They can be moved by **turtle** and repositioned, just 
like buckets.  

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Door.png) Door
These normally act like walls until **turtle** has the respectively-colored **key** to unlock the door. Once 
unlocked, the key used to unlock the door will be destroyed, so each key can only be used once.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Factory.png) Factory
This base can be used to clone any other object, when triggered by the respectively-colored **button**. These 
**factories** act like **walls** to other creatures.  (Note: by convention, the same colored factory to button would be
paired up, but this might not always be the case, i.e. if there are more factory-button mappings than there are colors.)

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Fire.png) Fire
This fiery tile can mercilessly kill most other moving creatures, including **turtle**. The only exception to this 
rule would be that a filled **bucket** would also extinguish the fire in the process, transforming it back into 
**sand**. 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Grass.png) Grass
This tall grass can block the view of any tiles under this grass. It could be anything under here, but it does not 
block the view of any other creatures. 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Portal.png) Portal
This is the exit-ticket in the level. However, you will have to collect enough food before exiting! If you try to go 
through the portal before consuming enough food, nothing will happen. Only **turtle** can go through this tile; 
anything else that tries to move across this portal will be stopped by some magical invisible force-field (citation 
needed)!

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Sand.png) Sand
Sand. It's just regular-old sand, nothing else.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Sign.png) Sign
Come to hear it's tale! Any object/creature can walk over it but only (maybe?) the **turtle** has the wisdom to be 
able to read the sign.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Trap.png) Trap
It's a trap! If some unsuspecting creature or object moves over this trap, it *immediately* gets destroyed to bits. 
At least the trap also gets destroyed. Watch out for these traps!

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Wall.png) Wall
It's just a wall. Nothing can pass through this dead wall.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Water.png) Water
Water. Pretty much this is the **turtle** safe zone because all other creatures (with the exception of **Plastic**) 
will drown or die in water.

### Items
Useful stuff that can be used by **turtle** to solve the level.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Food.png) Food
Consisting of grain, seaweed, and some fish, this food is vital sustenance for **turtle**. **Turtle** must consume 
enough **food** in order to be able to go to the **portal** and proceed to the next level. 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Key.png) Key
This can be paired up with the respectively-colored **door** to unlock it.

### Creatures
Creatures are all the living moving objects including our **turtle**. Most creatures, however, are very ruthless and 
are out to kill **turtle**.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Bird.png) Bird
This feathered creature, although a dignified and apparently harmless creature, will in fact harm **turtle**. It will 
try to fly as close to the turtle before gobbling it up, showing no mercy to this turtle. It, however, cannot fly 
into the **water** nor into the **fire**.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Child.png) Child
These innocent looking "devils" can prove to be super deadly to **turtle**. Although they seen to normally maneuver 
slowly, whenever they spot **turtle** down their line of sight, they will charge double speed (compared to the 
turtle) until they collide into some obstacle or **turtle**! Watch out for these creatures. 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Lawnmower.png) Lawnmower
This is a machine that has gone astray. Cursed to move back and forth indefinitely (it will recoil back when it 
reaches a wall), it lost it's original purpose of mowing **grass**, but rather it could only mow and destroy 
**turtle** if the **turtle** gets in its path. It will surely short-circuit in **water** and it will be burned under 
**fire**.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Plastic.png) Plastic Wrap
This floaty devious creature can kill an unsuspecting **turtle** if the turtle crosses it's path and consumes it. 
This plastic wrap will float in **water**, but once it reaches **sand**, it will be immobilized. 

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Ball.png) Projectile
These projectiles are created out of a **cannon**. This destroys any moving creatures along it's path including other 
cannon balls. When it hits a  corner, it will bounce off in a clockwise direction (i.e. it will try  to turn right if
it can). If it fails it will turn left if it can. Otherwise it will just recoil back.

#### ![](https://raw.githubusercontent.com/theKidOfArcrania/Mission-Turtle/master/img/Turtle.png) Turtle
It's you! You can control this character and guide the turtle through the game.

## License
Licensed under MIT.