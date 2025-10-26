package cc.dreamcode.bans.config;

import cc.dreamcode.menu.bukkit.BukkitMenuBuilder;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Configuration(child = "config.yml")
@Header("## Dream-Bans (Main-Config) ##")
public class PluginConfig extends OkaeriConfig {


  @Comment
  @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wylaczyc. :P")
  @CustomKey("debug")
  public boolean debug = false;

  @Comment
  @Comment("Ponizej znajduja sie dane do logowania bazy danych:")
  @CustomKey("storage-config")
  public StorageConfig storageConfig = new StorageConfig("storage");

  @CustomKey("mute.blocked-commands")
  public List<String> muteBlockedCommands = List.of(
      "/msg",
      "/tell",
      "/whisper",
      "/r",
      "/mail"
  );

  @CustomKey("ban.protected-permission")
  public String BanProtectedPermission = "dream-chat.protected";

  @CustomKey("blacklist.players")
  public List<String> blacklistPlayers = List.of(
      "Notch",
      "Herobrine"
  );

  @CustomKey("discordWebhookUrl")
  public String discordWebhookUrl = "";

  @Comment
  @Comment("Konfiguracja menu historii kar.")
  @CustomKey("check-ban-menu")
  public CheckBanMenuConfig checkBanMenu = new CheckBanMenuConfig(); // <-- ZMIANA

  @Comment
  @Comment("Konfiguracja przedmiotów wyświetlanych w menu /check.")
  @CustomKey("punishment-items")
  public PunishmentItemsConfig punishmentItems = new PunishmentItemsConfig();

  // vvv NOWA KLASA KONFIGURACYJNA DLA MENU vvv
  @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
  public static class CheckBanMenuConfig extends OkaeriConfig {

    @Comment("Tytul wspiera placeholder {name} - nazwe gracza.")
    public BukkitMenuBuilder menuBuilder = new BukkitMenuBuilder(
        "&cHistoria kar: &f{name}",
        6,
        Collections.emptyMap()
    );

    @Comment({
        "Przedmiot podsumowania kar na środku menu.",
        "Dostępne placeholdery: {name}, {bans_size}, {tempbans_size}, {mutes_size}, {tempmutes_size}, {ipbans_size}, {kicks_size}"
    })
    public ItemStack summaryItem = new ItemBuilder(Material.PAPER)
        .setName("&ePodsumowanie kar")
        .setLore(
            "&7Bany: &c{bans_size}",
            "&7TempBany: &c{tempbans_size}",
            "&7Mute: &c{mutes_size}",
            "&7TempMute: &c{tempmutes_size}",
            "&7BanIP: &c{ipbans_size}",
            "&7Kicki: &c{kicks_size}"
        ).toItemStack();

    @Comment("Przycisk poprzedniej strony.")
    public ItemStack previousPageItem = new ItemBuilder(Material.ARROW)
        .setName("&aPoprzednia strona")
        .toItemStack();

    @Comment("Przycisk następnej strony.")
    public ItemStack nextPageItem = new ItemBuilder(Material.ARROW)
        .setName("&aNastępna strona")
        .toItemStack();
  }

  @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
  public static class PunishmentItemsConfig extends OkaeriConfig {

    @Comment("Przedmiot dla permanentnego bana (używa {until_or_permanent})")
    public ItemStack ban = new ItemBuilder(Material.BOOK)
        .setName("&cBan")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{bannedBy}",
            "&7Data: &f{date}",
            "&7Do: &f{until_or_permanent}"
        ).toItemStack();

    @Comment("Przedmiot dla czasowego bana (używa {until})")
    public ItemStack tempBan = new ItemBuilder(Material.ENCHANTED_BOOK)
        .setName("&cTempBan")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{bannedBy}",
            "&7Od: &f{date}",
            "&7Do: &f{until}"
        ).toItemStack();

    @Comment("Przedmiot dla permanentnego wyciszenia (lore 'Do' jest stałe)")
    public ItemStack mute = new ItemBuilder(Material.PAPER)
        .setName("&7Mute")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{mutedBy}",
            "&7Data: &f{date}",
            "&7Do: &f&cPermanentny"
        ).toItemStack();

    @Comment("Przedmiot dla czasowego wyciszenia (używa {until})")
    public ItemStack tempMute = new ItemBuilder(Material.WRITABLE_BOOK)
        .setName("&7TempMute")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{mutedBy}",
            "&7Od: &f{date}",
            "&7Do: &f{until}"
        ).toItemStack();

    @Comment("Przedmiot dla bana na IP (używa {until_or_permanent})")
    public ItemStack ipBan = new ItemBuilder(Material.BEDROCK)
        .setName("&4BanIP")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{bannedBy}",
            "&7Data: &f{date}",
            "&7Do: &f{until_or_permanent}"
        ).toItemStack();

    @Comment("Przedmiot dla wyrzucenia z serwera")
    public ItemStack kick = new ItemBuilder(Material.IRON_BOOTS)
        .setName("&bKick")
        .setLore(
            "&7Powód: &f{reason}",
            "&7Przez: &f{kickedBy}",
            "&7Data: &f{date}"
        ).toItemStack();
  }
}