package cc.dreamcode.bans.profile;

import cc.dreamcode.bans.punishments.Ban;
import cc.dreamcode.bans.punishments.IpBan;
import cc.dreamcode.bans.punishments.Kick;
import cc.dreamcode.bans.punishments.Mute;
import cc.dreamcode.bans.punishments.TempBan;
import cc.dreamcode.bans.punishments.TempMute;
import cc.dreamcode.bans.punishments.Warn;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.persistence.PersistencePath;
import eu.okaeri.persistence.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("checkstyle:RightCurly")
public class Profile extends Document {

  public Profile() {}

  public Profile(UUID uuid) {
    this.setPath(PersistencePath.of(uuid.toString()));
  }

  @Setter @Getter @CustomKey("name")       private String name;
  @Setter @Getter @CustomKey("banned")     private boolean banned;
  @Setter @Getter @CustomKey("ban-reason") private String banReason;
  @Setter @Getter @CustomKey("banned-by")  private String bannedBy;
  @Getter @Setter @CustomKey("ban-until")  private long banUntil;

  @Setter @Getter @CustomKey("last-ip")    private String lastIp;

  @Getter @CustomKey("muted")              private boolean muted;
  @Getter @CustomKey("mute-reason")        private String muteReason;
  @Getter @CustomKey("muted-by")           private String mutedBy;
  @Getter @Setter @CustomKey("mute-until") private long muteUntil;

  @CustomKey("bans")      private List<Ban>     bans        = new ArrayList<>();
  @CustomKey("tempbans")  private List<TempBan> tempBans    = new ArrayList<>();
  @CustomKey("kicks")     private List<Kick>    kicks       = new ArrayList<>();
  @CustomKey("mutes")     private List<Mute>    mutes       = new ArrayList<>();
  @CustomKey("tempmutes") private List<TempMute> tempMutes  = new ArrayList<>();
  @CustomKey("ip-bans")   private List<IpBan>   ipBansCheck = new ArrayList<>();
  @CustomKey("warns")     private List<Warn>    warns       = new ArrayList<>();

  public List<Ban> getBans() { if (bans == null) bans = new ArrayList<>(); return bans; }
  public List<TempBan> getTempBans() { if (tempBans == null) tempBans = new ArrayList<>(); return tempBans; }
  public List<Kick> getKicks() { if (kicks == null) kicks = new ArrayList<>(); return kicks; }
  public List<Mute> getMutes() { if (mutes == null) mutes = new ArrayList<>(); return mutes; }
  public List<TempMute> getTempMutes() { if (tempMutes == null) tempMutes = new ArrayList<>(); return tempMutes; }
  public List<IpBan> getIpBansCheck() { if (ipBansCheck == null) ipBansCheck = new ArrayList<>(); return ipBansCheck; }
  public List<Warn> getWarns() { if (warns == null) warns = new ArrayList<>(); return warns; }

  public UUID getUniqueId() { return this.getPath().toUUID(); }

  public void ban(String reason, String admin) { this.banned = true; this.banReason = reason; this.bannedBy = admin; }
  public void unban() { this.banned = false; this.banReason = null; this.bannedBy = null; this.banUntil = 0; }
  public void mute(String reason, String admin) { this.muted = true; this.muteReason = reason; this.mutedBy = admin; }
  public void unmute() { this.muted = false; this.muteReason = null; this.mutedBy = null; this.muteUntil = 0; }

  public void banIp(String ip, String reason, String admin) {
    IpBan ipBan = new IpBan(ip, reason, System.currentTimeMillis(), admin, 0L);
    this.getIpBansCheck().add(ipBan);
  }
  public void unbanIp(String ip) { this.getIpBansCheck().removeIf(b -> b.getIp().equals(ip)); }
  public boolean hasIpBan(String ip) {
    return this.getIpBansCheck().stream().anyMatch(b -> b.getIp().equals(ip) && (b.getUntil() == 0L || b.getUntil() > System.currentTimeMillis()));
  }
}
