package cc.dreamcode.bans.config;

import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Headers;

@Configuration(child = "message.yml")
@Headers({@Header("## Dream-Tpa (Message-Config) ##"),
    @Header("Dostepne type: (DO_NOT_SEND, CHAT, ACTION_BAR, SUBTITLE, TITLE, TITLE_SUBTITLE)")})
public class MessageConfig extends OkaeriConfig {

  @CustomKey("command-usage")
  public BukkitNotice usage = BukkitNotice.chat("&7Przyklady uzycia komendy: &c{label}");
  @CustomKey("command-usage-help")
  public BukkitNotice usagePath = BukkitNotice.chat("&f{usage} &8- &7{description}");

  @CustomKey("command-usage-not-found")
  public BukkitNotice usageNotFound = BukkitNotice.chat(
      "&cNie znaleziono pasujacych do kryteriow komendy.");
  @CustomKey("command-path-not-found")
  public BukkitNotice pathNotFound = BukkitNotice.chat(
      "&cTa komenda jest pusta lub nie posiadasz dostepu do niej.");
  @CustomKey("command-no-permission")
  public BukkitNotice noPermission = BukkitNotice.chat("&cNie posiadasz uprawnien.");
  @CustomKey("command-not-player")
  public BukkitNotice notPlayer = BukkitNotice.chat(
      "&cTa komende mozna tylko wykonac z poziomu gracza.");
  @CustomKey("command-not-console")
  public BukkitNotice notConsole = BukkitNotice.chat(
      "&cTa komende mozna tylko wykonac z poziomu konsoli.");
  @CustomKey("command-invalid-format")
  public BukkitNotice invalidFormat = BukkitNotice.chat(
      "&cPodano nieprawidlowy format argumentu komendy. ({input})");

  @CustomKey("player-not-found")
  public BukkitNotice playerNotFound = BukkitNotice.chat("&cPodanego gracza nie znaleziono.");
  @CustomKey("world-not-found")
  public BukkitNotice worldNotFound = BukkitNotice.chat("&cPodanego swiata nie znaleziono.");
  @CustomKey("cannot-do-at-my-self")
  public BukkitNotice cannotDoAtMySelf = BukkitNotice.chat("&cNie mozesz tego zrobic na sobie.");
  @CustomKey("number-is-not-valid")
  public BukkitNotice numberIsNotValid = BukkitNotice.chat("&cPodana liczba nie jest cyfra.");

  @CustomKey("config-reloaded")
  public BukkitNotice reloaded = BukkitNotice.chat("&aPrzeladowano! &7({time})");
  @CustomKey("config-reload-error")
  public BukkitNotice reloadError = BukkitNotice.chat(
      "&cZnaleziono problem w konfiguracji: &6{error}");


  @CustomKey("ban.success")
  public BukkitNotice banSuccess = BukkitNotice.chat(
      "&aZbanowałeś gracza &f{player} &a(powód: {reason})");

  @CustomKey("ban.notify")
  public BukkitNotice banNotify = BukkitNotice.chat("&cZostałeś zbanowany! Powód: {reason}");

  @CustomKey("unban.notify")
  public BukkitNotice unbanNotify = BukkitNotice.chat("&cZostałeś obanowany!");

  @CustomKey("ban.kick")
  public String banKick = "&cZostałeś zbanowany!&r\n&7Powód: &f{reason}\n&7Zbanowany przez: &f{bannedBy}";

  @CustomKey("tempban.kick")
  public String tempBanKick = "&cZostałeś zbanowany!&r\n&7Powód: &f{reason}\n&7Zbanowany przez: &f{bannedBy}\n&7Do: &f{banExpire}";

  @CustomKey("unban.success")
  public BukkitNotice unbanSuccess = BukkitNotice.chat("&aOdbanowałeś gracza &f{player}.");

  @CustomKey("ban.default-reason")
  public String defaultReason = "Złamałeś regulamin";

  @CustomKey("ban.tempban-success")
  public BukkitNotice tempBanSuccess = BukkitNotice.chat(
      "&aZbanowałeś tymczasowo gracza &f{player} &a(do {banExpire}) (powód: {reason})");

  @CustomKey("warn.success")
  public BukkitNotice warnNotify = BukkitNotice.chat(
      "&aOstrzeżono gracza &f{player} &azapowód: {reason}");

  @CustomKey("warn.actionBar")
  public BukkitNotice actionBarWarn = BukkitNotice.chat( // <-- POPRAWKA 1 (z actionBar na chat)
      "&cZostałeś ostrzeżony! &r&7Powód: &f{reason}");

  @CustomKey("kick.success")
  public BukkitNotice kickSuccess = BukkitNotice.chat(
      "&aWyrzuciłeś gracza &f{player} &a(powód: {reason})");

  @CustomKey("kick.format")
  public String kickFormat = "&cZostałeś wyrzucony z serwera! &r\n&7Powód: &f{reason}\n&7Przez: &f{issuer}";

  @CustomKey("kick.broadcast")
  public BukkitNotice kickBroadcast = BukkitNotice.chat(
      "&eGracz &c{player} &ezostał wyrzucony przez &c{issuer} &7(Powód: {reason})");

  @CustomKey("silentkick.success")
  public BukkitNotice silentKickSuccess = BukkitNotice.chat(
      "&aWyrzuciłeś (cicho) gracza &f{player} &a(powód: {reason})");


  @CustomKey("silentkick.notify")
  public BukkitNotice silentKickNotify = BukkitNotice.chat(
      "&7[&cSilent&7] &eGracz &c{player} &ezostał wyrzucony przez &c{issuer} &7(Powód: {reason})");

  @CustomKey("ban.silent-success")
  public BukkitNotice silentBanSuccess = BukkitNotice.chat(
      "&a[Cichy ban] Zbanowałeś gracza &f{player} &a(powód: {reason})");

  @CustomKey("ban.silent-notify")
  public BukkitNotice silentBanNotify = BukkitNotice.chat(
      "&7[&cSilent&7] &f{issuer} &7zbanował cicho gracza &f{player} &7(powód: {reason})");

  @CustomKey("ban.silent-tempban-success")
  public BukkitNotice silentTempBanSuccess = BukkitNotice.chat(
      "&a[Cichy tempban] Zbanowałeś tymczasowo gracza &f{player} &a(do {banExpire}) (powód: {reason})");

  @CustomKey("ban.silent-tempban-notify")
  public BukkitNotice silentTempBanNotify = BukkitNotice.chat(
      "&7[&cSilent&7] &f{issuer} &7tymczasowo zbanował &f{player} &7(do {banExpire}) (powód: {reason})");

  @CustomKey("ban.ip.success")
  public BukkitNotice ipBanSuccess = BukkitNotice.chat(
      "&aZbanowałeś gracza &f{player} &ana IP &f{ip} &a(powiązane konto). Powód: &c{reason}");

  @CustomKey("ban.ip.kick")
  public String ipBanKick = "&cZostałeś zbanowany na IP!\n&7Powód: &f{reason}\n&7Zbanowany przez: &f{bannedBy}";

  @CustomKey("ban.ip.temp-kick")
  public String tempIpBanKick = "&cZostałeś tymczasowo zbanowany na IP!\n&7Powód: &f{reason}\n&7Zbanowany przez: &f{bannedBy}\n&7Wygasa: &f{banExpire}";

  @CustomKey("ban.ip-unban-success")
  public BukkitNotice ipUnbanSuccess = BukkitNotice.chat("&aZdjąłeś bana IP gracza &f{player}");

  @CustomKey("ban.unban-ip-success")
  public BukkitNotice unbanIpSuccess = BukkitNotice.chat(
      "&aOdbanowano IP gracza &f{player} &aprzez &f{issuer}");

  @CustomKey("ban.unban-ip-fail")
  public BukkitNotice unbanIpFail = BukkitNotice.chat(
      "&cNie udało się znaleźć IP gracza &f{player}.");

  @CustomKey("ban.no-ip-found")
  public BukkitNotice noIpFound = BukkitNotice.chat(
      "&cNie znaleziono ostatniego IP gracza &f{player}.");

  // ==== MUTE ====
  @CustomKey("mute.success")
  public BukkitNotice muteSuccess = BukkitNotice.chat(
      "&aGracz &f{player} &azostał wyciszony przez &f{mutedBy}&a. Powód: &f{reason}");

  @CustomKey("mute.fail")
  public BukkitNotice muteFail = BukkitNotice.chat("&cNie udało się wyciszyć gracza &f{player}.");

  @CustomKey("mute.notify")
  public BukkitNotice muteNotify = BukkitNotice.chat("&cZostałeś wyciszony! Powód: &f{reason}");

  @CustomKey("mute.chat-blocked")
  public BukkitNotice muteChat = BukkitNotice.chat(
      "&cNie możesz pisać, ponieważ jesteś wyciszony. Powód: &f{reason} &7(przez: {mutedBy})");


  // ==== TEMP MUTE ====
  @CustomKey("tempmute.success")
  public BukkitNotice tempMuteSuccess = BukkitNotice.chat(
      "&aGracz &f{player} &azostał wyciszony do &f{muteExpire} &aprzez &f{mutedBy}&a. Powód: &f{reason}");

  @CustomKey("tempmute.fail")
  public BukkitNotice tempMuteFail = BukkitNotice.chat(
      "&cNie udało się tymczasowo wyciszyć gracza &f{player}.");

  @CustomKey("tempmute.notify")
  public BukkitNotice tempMuteNotify = BukkitNotice.chat(
      "&cZostałeś wyciszony do &f{muteExpire}&c! Powód: &f{reason}");

  @CustomKey("tempmute.chat-blocked")
  public BukkitNotice tempMuteChat = BukkitNotice.chat(
      "&cNie możesz pisać, jesteś wyciszony do &f{muteExpire}&c. Powód: &f{reason} &7(przez: {mutedBy})");

  @CustomKey("unmute.success")
  public BukkitNotice unmuteSuccess = BukkitNotice.chat("&aGracz &f{player} &azostał odmutowany.");

  @CustomKey("unmute.fail")
  public BukkitNotice unmuteFail = BukkitNotice.chat(
      "&cNie udało się odmutować gracza &f{player}.");

  @CustomKey("unmute.notify")
  public BukkitNotice unmuteNotify = BukkitNotice.chat("&aZostałeś odmutowany!");

  @CustomKey("mute.command-blocked")
  public BukkitNotice muteCommandBlocked = BukkitNotice.chat(
      "&cNie możesz używać tej komendy, gdy jesteś wyciszony.");

  @CustomKey("ban.protected-player")
  public BukkitNotice banProtected = BukkitNotice.chat(
      "&cNie możesz zbanować gracza &f{player}&c, ponieważ jest chroniony.");

  @CustomKey("blacklist.kick")
  public String blacklistKick = "&cZostałeś dodany do czarnej listy!";

  @CustomKey("ban.broadcast")
  public String banBroadcast = "&aGracz &f{player} &azostał zbanowany przez &f{bannedBy}";

  @CustomKey("mute.broadcast")
  public String muteBroadcast = "&aGracz &f{player} &azostał wyciszony przez &f{mutedBy}";

  @CustomKey("tempmute.broadcast")
  public String tempMuteBroadcast = "&aGracz &f{player} &azostał tymczasowo wyciszony przez &f{mutedBy} &a(do {muteExpire})";

  @CustomKey("ipban.broadcast")
  public String ipBanBroadcast = "&cGracz &f{player} &czostał zbanowany po IP przez &f{bannedBy}";

  @CustomKey("tempban.broadcast")
  public String tempBanBroadcast = "&cGracz &f{player} &czostał tymczasowo zbanowany przez &f{bannedBy} &c(do {banExpire})";

  @CustomKey("multiAccount.alert")
  public BukkitNotice multiAccountAlert = BukkitNotice.chat(
      "&c⚠ Gracz &f{joining} &cłączy się z IP, na którym zarejestrowane jest inne konto - &f{banned}!");

  @CustomKey("errorWhenLoadingProfile.notify")
  public BukkitNotice errorWhenLoadingProfile = BukkitNotice.chat("&aerrorWhenLoadingProfile!");;
}