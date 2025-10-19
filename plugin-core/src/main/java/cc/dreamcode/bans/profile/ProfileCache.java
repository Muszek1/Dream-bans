package cc.dreamcode.bans.profile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class ProfileCache {

  private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();
  private final Map<String, UUID> names = new ConcurrentHashMap<>();

  private static String key(@NonNull String name) {
    return name.toLowerCase();
  }

  public void add(@NonNull Profile profile) {
    profiles.put(profile.getUniqueId(), profile);
    if (profile.getName() != null) {
      names.put(key(profile.getName()), profile.getUniqueId());
    }
  }

  public void remove(@NonNull UUID uuid) {
    Profile removed = profiles.remove(uuid);
    if (removed != null && removed.getName() != null) {
      names.remove(key(removed.getName()));
    }
  }

  public Profile get(@NonNull UUID uuid) {
    return profiles.get(uuid);
  }

  public Profile get(@NonNull Player player) {
    return profiles.get(player.getUniqueId());
  }

  public Profile get(@NonNull String name) {
    UUID uuid = names.get(key(name));
    return uuid != null ? profiles.get(uuid) : null;
  }

  public boolean contains(@NonNull UUID uuid) {
    return profiles.containsKey(uuid);
  }

  public Map<UUID, Profile> all() {
    return profiles;
  }
}
