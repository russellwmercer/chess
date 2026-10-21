package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {

    List<GameData> listGames() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    int createGame(GameData gameData) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;

}
