# Arena game web-server 


The current version (Development Phase 2) supports the creating and storing a character, creating a battle with an automatically selected bot of the appropriate level, causing/receiving damage, and also completing the battle with recording the gained experience to the character's state (database).

## How to run

Before starting make sure to have docker and the sbt utility installed.

To run the application you need to:
1. Clone the repository
2. The project uses a primitive database with one table to store the state of characters. So before starting the server you need to make sure that PostgreSQL is running and apply the initial migrations - you can use docker-compose to start the database and apply initial migrations, just type `docker-compose up -d` in the terminal of the project root.
3. Create a configuration file in the project root with the `application.conf` name. You may just copy and rename the `application.template.conf` file if you start the database with docker-compose as described above.
4. Run `sbt run` from the project root. The application runs on port 8080.

To run tests call `sbt test` from the project root.

## API

Before starting a battle you need to create a new character or get an existing one.
- `/character/allCharacters` endpoint gives you information about all the existing characters
- `/character/newCharacter/{providedName}` endpoint adds a new character to the database. For example, `http://localhost:8080/character/newCharacter/PumpkinJack` creates a character named `PumpkinJack` and may return the following response

```
{"id":"69149542-e3e3-4e01-9170-5a5ef3179c42","name":"PumpkinJack","experience":0}
```

- `/character/getCharacter/{characterId}` endpoint returns the information about the character with provided id if it exists in database. For example, `http://localhost:8080/character/getCharacter/69149542-e3e3-4e01-9170-5a5ef3179c42` may return the response as shown above.
- `/character/deleteCharacter/{characterId}` endpoint deletes characters from the database if it exists and returns the deleted information. For example, the `http://localhost:8080/character/deleteCharacter/69149542-e3e3-4e01-9170-5a5ef3179c42` request deletes PumpkinJack from the database.


After you get a character's id you may start a battle. 
 - `/start/{characterId}` - the endpoint creates a new battle for the provided character and a bot of the corresponding level. The response contains the battle id. Suppose, we have a character with id `d605b498-c4c0-4436-a473-90ca733b2493`. so we may type `http://localhost:8080/start/d605b498-c4c0-4436-a473-90ca733b2493` in our browser to create a battle. We get the battle id in response (for example `a105b86a-c0b5-4444-82b8-cc8451beeb15`)
 - `/hit/{battleId}` - the endpoint involves causing damage to all participants of the battle (which id must be provided). The response may be either a new battle state or a message about a winner. For example, the request may be `http://localhost:8080/hit/a105b86a-c0b5-4444-82b8-cc8451beeb15` and the response may be `The battle is over, Melody won`. 
 If you pass a non-existent ID to the endpoint, the server will return the 404 error.
 - `/completeBattle/{battleId}` - the endpoint removes the battle with provided id from the game state and updates the character information in database (adds experience). For example, the request is `http://localhost:8080/completeBattle/a105b86a-c0b5-4444-82b8-cc8451beeb15` and the response will be `The battle is over, Melody won. 11 experience received`. If you pass a non-existent ID to the endpoint, the server will return the 404 error.
 
 There are also two additional getter-like endpoints that give information about battle state and game state respectively: `/getBattle/{battleId}` and `/allBattles` 

## Roadmap

### Phase 1 (base functionality) - completed
1. The ability to create a battle for a first level character with a bot by passing the character name
2. GameState as InMemoryGameState with all the active battles
3. Implementation of the following endpoints:
  - allBattles()
  - start(name: String)
  - getBattle(battleId: UUID)
  - hit(battleId: UUID)
  - delete(battleId: UUID)

### Phase 2 (Character logic improvements and Character storage) - completed
1. Add Database (probably PostgreSQL) to store characters
2. Add experience and level growth for Characters
3. Battle creation for a character and a bot of the corresponding level
4. Endpoints for creating, fetching and deleting characters

### Phase 3 (Game and Battle improvements) - In Progress
1. The ability to create either a battle with a bot or a battle with another character - add a concurrent battle processing
2. The abilities to wait for an enemy and join a battle
3. Corresponding endpoints the new functionality

### Phase 4 (Authentication and binding of characters to the user)
...

