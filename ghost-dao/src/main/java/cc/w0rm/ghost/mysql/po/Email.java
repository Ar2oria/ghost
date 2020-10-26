package cc.w0rm.ghost.mysql.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private Integer id;

    private Long qqAccount;

    private String joinedGroups;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getQqAccount() {
        return qqAccount;
    }

    public void setQqAccount(Long qqAccount) {
        this.qqAccount = qqAccount;
    }

    public String getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(String joinedGroups) {
        this.joinedGroups = joinedGroups == null ? null : joinedGroups.trim();
    }
}