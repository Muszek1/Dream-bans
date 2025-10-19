package cc.dreamcode.bans.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IpBan {

  private final String ip;
  private final String bannedBy;
  private final long until;
  private String reason;
  private long date;

}
