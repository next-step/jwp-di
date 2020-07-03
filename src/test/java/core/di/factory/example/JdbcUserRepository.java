package core.di.factory.example;

import core.annotation.Lazy;
import core.annotation.Repository;

@Lazy
@Repository
public class JdbcUserRepository implements UserRepository {
}
