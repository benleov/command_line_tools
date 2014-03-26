package webservicesapi.command.impl.favorite;

/**
 * Called when a favorite has been modified
 */
public interface FavoriteListListener {



    void onFavoriteChange(ChangeType type, String alias);
}
