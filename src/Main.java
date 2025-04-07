import class_logic.User;
import db.UserDAOImplementation;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // USERDAO TEST
        UserDAOImplementation user = new UserDAOImplementation();
        List<User> user_list = user.search(1);
        user_list.forEach(User::print);
    }
}