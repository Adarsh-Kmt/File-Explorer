package kamathadarsh.FileExplorer.JOOQRepository;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.GROUP_TABLE;

@Repository
@AllArgsConstructor
public class JOOQGroupRepository {

    private final DSLContext dslContext;

    public void checkIfGroupExists(String groupName){

        List<String> groupExists = dslContext.select(GROUP_TABLE.GROUP_NAME)
                .from(GROUP_TABLE)
                .where(GROUP_TABLE.GROUP_NAME.eq(groupName))
                .fetchInto(String.class);

        if(groupExists.isEmpty()){

            dslContext.insertInto(GROUP_TABLE)
                    .set(GROUP_TABLE.GROUP_NAME, groupName)
                    .execute();
        }
    }
}
