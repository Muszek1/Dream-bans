package cc.dreamcode.bans.punishments;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Ban extends OkaeriConfig {

  private final String bannedBy;
  private final long until;
  private String reason;
  private long date;

}
