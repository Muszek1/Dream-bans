package cc.dreamcode.bans.punishments;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempMute extends OkaeriConfig {

  private String mutedBy;
  private long until;
  private String reason;
  private long date;
}
