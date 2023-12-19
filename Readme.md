# Arena game web-server 

The current version (Development Phase 1) implements the creation of a battle with a bot for a first-level player, causing/receiving damage, as well as removing the battle from the program state. Additional functionality will be implemented in the following development phases (see roadmap below).

## How to run

Before running make sure to have sbt utility installed.
To run the application you need to:
1. Clone the repository
2. Run `sbt run` from the project root. The application will run on port 8080.

To run flow tests run `sbt test` from the project root.

## API

The current version supports the following endpoints:
 - `/start/{providedName}` - the endpoint creates a new battle for a first-level player (whose name must be provided by client) and a first-level bit. The response contains the battle id. For example, you may type `http://localhost:8080/start/Melody` in your browser to create a battle between first-level player named "Melody" and a first-level bot. You will get the battle id in response (for example `a105b86a-c0b5-4444-82b8-cc8451beeb15`)
 - `/hit/{battleId}` - the endpoint involves causing damage to all participants of the battle (which id must be provided). The response may be either a new battle state or a message about a winner. For example, the request may be `http://localhost:8080/hit/a105b86a-c0b5-4444-82b8-cc8451beeb15` and the response may be `The fight is over, Melody won`. 
 If you pass a non-existent ID to the endpoint, the server will return еру 404 error.
 - `/delete/{battleId}` - the endpoint removes the battle with provided id from the game state. For example, the request is `http://localhost:8080/delete/a105b86a-c0b5-4444-82b8-cc8451beeb15` and the response will be `a105b86a-c0b5-4444-82b8-cc8451beeb15 fight deleted successfully`. If you pass a non-existent ID to the endpoint, the server will return еру 404 error.
 
 There are also two additional getter-like endpoints that give information about battle state and game state respectively: `/getFight/{battleId}` and `/allFights` 

## Roadmap

### Phase 1 (base functionality)
1. The ability to create a battle for a first level player with a bot by passing the character name
2. GameState as InMemoryGameState with all the active battles
3. Implementation of the following endpoints:
  - allFights()
  - start(name: String)
  - getFight(battleId: UUID)
  - hit(battleId: UUID)
  - delete(battleId: UUID)

### Phase 2 (Player logic improvements and Player storage)
1. Add Database (probably PostgreSQL) to store characters
2. Add experience and level growth for Players
3. Battle creation for a player and a bot of the corresponding level
4. Endpoints for creating, fetching and deleting characters

### Phase 3 (Game and Battle improvements)
1. The ability to create either a battle with a bot or a battle with another player
2. The abilities to wait for an enemy and to join the battle
3. Corresponding endpoint the new functionality

...

