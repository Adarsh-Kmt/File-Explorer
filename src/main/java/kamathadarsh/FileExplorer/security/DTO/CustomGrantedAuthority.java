package kamathadarsh.FileExplorer.security.DTO;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.GroupTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomGrantedAuthority implements GrantedAuthority {

    private String groupName;
    @Override
    public String getAuthority() {
        return groupName;
    }
}
