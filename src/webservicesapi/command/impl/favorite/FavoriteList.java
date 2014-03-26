package webservicesapi.command.impl.favorite;

import java.util.*;

/**
 * Holds the list of favorite commands.
 */
public class FavoriteList {

    private Map<String, Favorite> favorites;
    private Set<FavoriteListListener> listeners;

    public FavoriteList() {
        favorites = new HashMap<String, Favorite>();
        listeners = new HashSet<FavoriteListListener>();
    }

    public Favorite getFavorite(String name) {
        return favorites.get(name);
    }

    public Favorite removeFavorite(String alias) {

        try {
            return favorites.remove(alias);
        } finally {
            fireFavoriteChange(ChangeType.REMOVE, alias);
        }
    }

    public Favorite addFavorite(String alias, String command, String params) {

        boolean exists = favorites.containsKey(alias);

        try {
            Favorite favorite = new Favorite(alias, command, params);
            return favorites.put(alias, favorite);
        } finally {

            if (exists) {
                fireFavoriteChange(ChangeType.MODIFY, alias);
            } else {
                fireFavoriteChange(ChangeType.ADD, alias);
            }
        }
    }

    public void addListener(FavoriteListListener listener) {
        listeners.add(listener);
    }

    protected void fireFavoriteChange(ChangeType type, String alias) {
        for (FavoriteListListener curr : listeners) {
            curr.onFavoriteChange(type, alias);
        }
    }

    public Map<String, Favorite> getAllFavorites() {
        return Collections.unmodifiableMap(favorites);
    }
}
