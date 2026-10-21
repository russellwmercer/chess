package dataaccess;

import model.GameData;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public int createGame(GameData gameData) {
        int id = nextGameID++;
        GameData withGameID = new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        games.put(id, withGameID);
        // This function returns the game ID after it saves it into the hashmap.
        return id;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (!games.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() {
        games.clear();
        nextGameID = 1;
    }
}
