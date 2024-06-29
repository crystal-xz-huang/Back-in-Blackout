# Week 1
## Requirements Analysis
From the problem and specification, I used noun/verb analysis to identify that the possible entitites, attributes and behaviours and find the ubiquitous language of the domain "Blackout System":

**Entities/Classes**
- Entity
    - Device (Handheld, Laptop, Desktop)
    - Satellite (Standard, Teleporting, Relay)
- File
- Jupiter

**Attributes**
- File
    - filename
    - size
    - content
- Device
    - id
    - location (position)
    - range
    - stored files
- Satellite
    - id
    - location (height, position)
    - range
    - velocity
    - direction
    - file capacity
    - storage capacity
    - stored files
    - bandwith (sending and receiving)

**Behaviours**
- Device and Satellite Communication (File Transferring)
    - *Upload/Download/Send* Files
- Device
    - *Supports* a subset of satellites
    - *Connects* to a Satellite
    - *Static* and do not move
- Satellite
    - *Supports* a subset of devices and satellites
    - *Connects* to a Satellite/Device
    - *Orbits* around Jupiter

## Initial Design and Setup
To decide on a initial design, I used a UML class diagram to model my identitifed entities and attributes.
I decided that Satellite and Device should each be an abstract base class for more specific types like RelaySatellite, LaptopDevice etc to extend from.
File should be a concrete base class since there are no different file types.

Overall, the initial setup was:
- BlackoutController
- File
- *Satellite*
    - RelaySatellite
    - StandardSatellite
    - TeleportingSatelite
- *Device*
    - HandheldDevice
    - LaptopDevice
    - DesktopDevice

## Change 1
During the implementation of methods for Task 1, I noticed that my implementation for retrieving device/satellite information in `public EntityInfoResponse getInfo(String id);` was rather long and a code smell (long method) since I needed to iterate through the list of devices to check if the given id correlated to a deviceId and if not, then iterate over the list of satellites.

The first refactoring solution I could think of was to extract the logic for finding the given id to 2 new methods - one for getting the corresponding Satellite given an id and the other for getting the corresponding Device.

However, from my UML diagram, I noticed that Devices and Satellites both share many common attributes (id, height, position, range, files) as well as behaviours (file transferring). Therefore, I decided to encapsulate these common fields and methods into a new abstract base class called `Entity`. Then the base classes `Satellite` and `Device` can extend from Entity and add more specific fields and methods.

I felt that this refactoring and change in design was a better design choice as:
- **Abstraction**: The `Entity` class can serve as an Abstract Data Type (ADT), abstracting the specifics of different entity types in the system and only define the behaviours available.
- **Encapsulation**: The `Entity` class can serve as a base class for both `Satellite` and `Device`, encapsulating common code and ensuring that shared fields and methods are centralised, thereby promoting code reuse and making the system easier to maintain.
- **Single Responsibility Principle**: The `Entity` class is responsible for managing common attributes and behaviors, while the `Satellite` and `Device` classes focus on their specific functionalities.
- **Inheritance**: A logical hierarchy is created where `Satellite` and `Device` extend from `Entity` and more specific subtypes extend from `Satellite` and `Device`, respectively.
- **Open/Closed Principle**: The `Entity` is open for extension by subclasses `Satellite` and `Device` but closed for modification.

The new setup was:
- BlackoutController
- File
- *Entity*
    - *Satellite*
        - RelaySatellite
        - StandardSatellite
        - TeleportingSatelite
    - *Device*
        - HandheldDevice
        - LaptopDevice
        - DesktopDevice

## Change 2
My Satellite class constructor had a long parameter list of more than 5 (code smell), since satellites had extra attributes added on from Entity (height, send bandwith, receive bandwith, send bandwith, receive bandwidth, file capacity, storage capacity). To reduce the length of parameters to around 5, I decided to make use of abstract getter methods such as `getSendBandwidth()`, `getReceiveBandwidth()`, `getVelocity()` etc to enforce the contract that the subclasses must implement these methods and therefore, they must return these attributes. This meant that I can remove these parameters from the constructor parameter list, therefore resolving the code smell of a long parameter list.

## Change 3
To implement `public void simulate();` which involves moving satellites, I decided to create an interface `Orbit` (as identified from the Requirements Analysis) to enforce a common contract for movement behaviour that the Satellite types can implement in their own specific ways.

My thought process was that "orbiting" is a behaviour and will only contain methods related to movement. From the spec, if I wanted to implement Task 3 Moving Devices, I can easily modify the system to have both Satellites and specific types of Device implement different orbiting behaviours without altering existing code. Furthermore, it encapsulates the common movement behaviour to one class and if I wanted to changes and additions related to orbit logic, these changes will be enforced across all classes that implement it without impacting other parts of the codebase, making the system more flexible, maintainable and robust.

Additionally, I realised that calculating the new position based on velocity, height, position and direction was same for all classes. THerefore, I added additional methods `getNewPosition` and `normalizeAngle` as static methods so that all classes implementing the Orbit interface can have access to it and use it in their own specific orbiting implementation, thereby reducing code duplication and promoting code reuse.

Furthermore, I decided that since the attributes `int direction` and `int velocity` were related to orbiting, I could remove these from the `Satellite` base class and define abstract methods `getDirection` and `getVelocity` in the `Orbit` interface to enforce that satellite subclasses must implement these methods and therefore, they must return these values.

## Change 4
My initial implementation for listing all entities in range for `public List<String> communicableEntitiesInRange(String id);` involved alot of helper methods which checked if the entities were supported, in range and visible to the source entity, and if not directly in range and visible if they have a relay path to the source entity.

I considered delegating the responsibility of these helper methods to the `Entity` itself. However, I speculated that moving the logic for checking if there was a relay path between two entities to the `Entity` class was a possible violation of SRP and Law of Demeter since I needed to pass the entire list of Relay Satellites in the system. My basis for creating the Entity class was to abstract the need to know the specific types of Entity there are in the system. Adding relay path logic into the Entity class would expose details about the types of entities and break the abstraction. Futhermore, it would mean that the Entity class would take on multiple responsibilities, including managing its state and determining connectivity with other entities, which is not in line with SRP. Lastly, it would increase dependencies and interactions between the `Entity` classes and this is not in line to the Law of Demeter which suggests that a method should only interact with its direct dependencies.

Therefore, I decided to move helper methods into a separate utility class `ConnectivityHelper`, to handle the logic of checking if entities were supported, in range, and visible, or if they had a relay path. This also reduced the amount of code in the blackout controller class and encapsulated the connectivity logic to a separate clas which made it easier to see and make modifications to the code related to listing all entities in range.

# Week 2
## Change 5
File transferring was the most challenging aspect to implement. For my current design, transferring a file from one entity to another meant that I needed a way to keep track of the source and destination entity of each file being transferred, have a record of which files are currently in transfer for each entity, differentiate between the outgoing vs incoming file transfers for each entity, and keep track of the amount of bytes currently transferred in each file.

My first approach was to add additional fields in the `File` class: `int fromId`, `int toId`, `int transferredBytes`. Then, I handled the file transferring logic in `BlackoutController`. This involved a multitude of helper methods, because I needed to iterate through the list of entities, access their list of files, iterate through their files, and get the fromId and toId information. Then, I iterated through the entities again to match the fromId and toId against their id to find the corresponding file. Not only did this involve several nested loops, but I also needed this nested loop for several methods to get the available send and receive bandwidth, get the available file/storage space, and update the transferredBytes. Furthermore, the amount of code in BlackoutController kept increasing, and it was extremely messy and difficult to understand. Worst of all, I couldn't even get the implementation right to pass the tests, and it was difficult to debug and see which part was incorrect due to the massive amount of interconnected logic.

## Change 6
My second approach was to delegate the file transferring responsibility to the Entity class itself. Similar to orbit, my idea was that file transferring was a behaviour common to entities, and I could create a `FileTransferring` interface to encapsulate common methods needed for file transferring, such as getting the list of outgoing and incoming transfers, checking the available storage and file space, and calculating the available bandwidth. However, since interfaces can only have abstract methods, the entity classes needed to implement these methods, which made the entity class extremely long (a code smell). Also, I had difficulty differentiating between complete, outgoing, and incoming transfers for each entity. I tried creating separate lists for complete, outgoing, and incoming files, but it didn't make much difference and didn't reduce the complexity. This meant that I needed more methods for accessing and modifying three lists instead of one. Lastly, it had minimal impact in reducing the complexity of the file transferring logic in `BlackoutController`.

## Change 7
My third approach was to extract and move all file transferring logic to a separate utility class outside of `BlackoutController`. While this reduced the amount of code in `BlackoutController`, it still didn't reduce the amount of code needed, and I still struggled with debugging and passing the tests.

# Week 3
## Change 8
After learning about Design Principles and Refactoring in Week 3, I went back to the UML class diagram and tried to map out the possible ways to have a composition relationship between `Entity` and `File`, and an aggregation relationship between the `File` and the `Entity` it's being transferred to. My new thought process was that the `File` class should only represent a file and its contents and not include file transferring logic, since a "file transfer" was different from a "file" - a file transfer represents a transfer that contains the file being transferred, the source and destination, and the transfer state and progress. Also, keeping the `fromId` and `toId` fields in the `File` class didn't make much sense since either `fromId` or `toId` would always be the Entity id the file is in.

I decided that I could create a new class `FileTransfer` to represent a file transfer. This class would include information about the `File` being transferred, the amount of bytes currently transferred, and the source and destination entities for this transfer. Then, I could move the file transferring methods from the Entity class to this new class.

I contemplated where to place the new `FileTransfer` class. I started by putting it in the `Entity` class itself. However, this resulted in a lot of coupling and overlap between the `File` and `FileTransfer` objects and their methods within each Entity. It also went added coupling with another `Entity` and broke abstraction within the `Entity` class. Furthermore, it did not significantly change my old implementation since the `FileTransfer` logic was still contained within the `Entity`.

## Change 9
I decided to keep the `FileTransfer` class in the `BlackoutController` class rather than inside the `Entity` class to encapsulate all logic related to file transfers within a single module. This decision was made to maintain a clear separation of concerns, enhance modularity, and adhere to design principles.

Since the `FileTransfer` class has a single responsibility: managing the file transfer process, keeping it in the `BlackoutController` class ensures that the `Entity` class remains focused on managing its own state and behaviour, such as storing and retrieving files, without taking on additional responsibilities related to file transfers. This separation maintains high cohesion within each class by ensuring they each have a well-defined, focused responsibility.

In addition, placing the `FileTransfer` class in the `BlackoutController` class maintains high cohesion by grouping together all actions related to file transfers. This approach makes the transfer logic easier to understand, maintain, and modify.

Moreover, keeping the `FileTransfer` logic outside the `Entity` class makes sense because file transferring changes the state of two entities. Since the `BlackoutController` class is responsible for managing the interaction and connectivity between multiple entities, it is more appropriate to handle file transfers at this level. This decision aligns with the responsibility of the BlackoutController class, ensuring that it manages inter-entity operations while Entity classes manage their internal states.

## Change 10
To further refine my design using the refactoring techniques from the lectures and keeping the principle of "favor composition over inheritance" in mind, I decided to add a `FileStorage` class to the `Entity` class to replace the list of files. This change involved moving methods related to file handling to the `FileStorage` class, promoting a cleaner and more modular design. I employed composition instead of extending the `Entity` class with file management capabilities. Delegating file-related responsibilities to the `FileStorage` class also made the codebase more maintable and clearer to understand. Also, I was able to group together the `int fileCapacity` and `int storageCapacity` fields and move them to the `FileStorage` class instead, along with their related methods which checks for available storage space. I then introduced `FileStorage` as Parameter Object in the Entity Constructor which allowed the subclasses to define the file capacity and storage capacity limits.

I simplified the logic for calculating the available bandwidth for each entity by adding a counter for the number of incoming files and outgoing files in the `FileStorage` class. I then introduced `FileStorage` as a parameter object in the `FileTransfer` class so that I'm able to make direct changes to the files and the counters (which affect the bandwidth) without violating the Law of Demeter.

## Change 11
To take advantage of inheritance fron the Entity class, I realised I could move a lot of the file transfer checks to this class. Since the `Entity` class defines abstract methods to get the available `sendBandwith` and `receiveBandwith`, I can utilise polymorphism to return the correct value at runtime and use the return of these abstract methods to implement the checks for `hasSendBandwidth`, `hasReceiveBandwidth`, as well as calculating the sending speed and receiving speed for file transfers. This reduced code duplication and also centralised the file transfer checks to one class.

## Change 12
The last change I made to my design was to create a `BlackoutSystem` class to represent the entire blackout system. This class is responsible for containing entities and handling the logic for modifying the state of entities and managing inter-entity operations. The `BlackoutController` class would then contain an instance of a `BlackoutSystem` object.

I thought using the `BlackoutSystem` class to serve as a high-level abstraction for managing the collection of entities and their interactions provided a clean and simple interface for the BlackoutController class to use. In addition, the `BlackoutSystem` encapsulated all the core functionalities related to managing entities and their interactions within a single class which was a clearer and more defined representation of the Blackout System itself. The Blackout System is divided into smaller, self-contained units (entities, file transfers) and promotes modularity by separating the concerns of entity management and system control. The `BlackoutSystem` class handles all entity-related logic, while the `BlackoutController` class manages the overall system flow and user interactions.

## Change 12
I decided to implement Task3 ElephantSatellite which introduced transient files during out-of-range file transfers. Adding the `ElephantSatellite` class itself was simple since my `Entity` and `Satellite` base classes were only responsible for setting and getting the attributes of the Elephant Satellite and I could use the already implemented `getNewPosition` method in Orbit to update the satellite position for moving. However, adding the new logic of transient files in file transferring proved to be more difficult.

I created a utility class `KnapsackSolver` to implement the 0-1 Knapsack Algorithm and return a list of files that can be kept stored to optimise the amount of transferred bytes in the Elephant Class.

I delegated the responsibility of removing transient files in the `FileStorage` class. This meant I needed to add a `isTransient` attribute to the `File` class in order to identiy transient files.

I then delegated the responsibility of managing transient file transfers which included setting a `File` as transient and making sure that the transfer is paused and doesnt take up available bandwidth in the `FileTransfer` class.

The noticable flaw in my design was that my initial simplication for calculating the available sending and receiving bandwidth through the addition of counters for the number of incoming and outgoing transfers, was not very effective for the addition of transient files. Everytime a file is marked as transient, I needed to make sure I decreased the number of incoming transfers in the destination entity and decrease the number of outgoing transfers in the source entity. I also needed to reverse this when a transient file resumes transfer. Furthermore, I needed to update these counters when a transient file is deleted. However, since I delegated the removal of transient files in the `FileStorage` class, this meant I needed to manually check if a transient file is deleted in the `FileTransfer` class and then update the counters. Then I needed to remove this transfer outside in `BlackoutSystem`. Overall, I'm not confident that my implementation of transient files in file transfers is robust and correct since it involved making changes across 3 classes.


# Reflection
Overall, I think I did ok on this assignment. I was happy with how I grouped the devices and satellites together into one Entity class, and how it simplified the management of both satellites and devices in the blackout system.

The biggest challenge was adhering to good design principles whilst implementing file transferring and aiming for a robust, highly cohesive, flexible and maintainable system where adding new functionality and changes doesn't involve breaking code across multiple classes. I definitely think my implementation of file transferring can be improved on, as adding transient file transfers for Task3 Elephant Satellite proved that changes were needed in several classes and these changes were closely intertwined with each other.

Through this assignment, I learnt refactoring techniques for reducing long classes and methods through delegation to other classes, composition and introducing parameter objects. I also learnt how to think about adhering to design principles whilst planning out the system and its overall hierarchy and relationships.

