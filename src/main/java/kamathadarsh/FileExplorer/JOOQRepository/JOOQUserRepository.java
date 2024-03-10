package kamathadarsh.FileExplorer.JOOQRepository;


import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.FileExplorer.request.RegisterUserRequest;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import static kamathadarsh.Conduit.jooq.jooqGenerated.tables.UserTable.USER_TABLE;

@Repository
@AllArgsConstructor
public class JOOQUserRepository {

    private final DSLContext dslContext;


    public boolean userExists(String username){
        return dslContext.fetchExists(dslContext.select()
                .from(USER_TABLE)
                .where(USER_TABLE.USERNAME.eq(username)));
    }

    public void registerNewUser(RegisterUserRequest request){

        dslContext.insertInto(USER_TABLE)
                .set(USER_TABLE.USER_GROUP_NAME, request.getGroupName())
                .set(USER_TABLE.PASSWORD, request.getPassword())
                .set(USER_TABLE.USERNAME, request.getUsername())
                .execute();
    }

    public Optional<UserTable> findUser(String username){

        return Optional.ofNullable(dslContext.selectFrom(USER_TABLE).where(USER_TABLE.USERNAME.eq(username)).fetchOneInto(UserTable.class));
    }

    public String getGroupName(String username){

        return dslContext.select(USER_TABLE.USER_GROUP_NAME)
                .from(USER_TABLE)
                .where(USER_TABLE.USERNAME.eq(username))
                .fetchOneInto(String.class);
    }
}
