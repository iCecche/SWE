package unit.model;

import db.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    private QueryBuilder builder;

    @BeforeEach
    void setUp() {
        builder = QueryBuilder.create();
    }

    @Test
    void create() {
        assertNotNull(builder);
        assertEquals(QueryBuilder.class, builder.getClass());
    }

    @Test
    void select() {
        builder.select();
        assertEquals("SELECT *", builder.getQuery());
    }

    @Test
    void selectOnly() {
        builder.select("COLUMN1", "COLUMN2");
        assertEquals("SELECT COLUMN1, COLUMN2", builder.getQuery());
    }

    @Test
    void insertInto() {
        builder.insertInto("TABLE", "COLUMN1", "COLUMN2");
        assertEquals("INSERT INTO TABLE (COLUMN1, COLUMN2)", builder.getQuery());
    }

    @Test
    void withEnumColumn() {

    }

    @Test
    void values() {
        insertInto();
        builder.values("VALUE1", "VALUE2");
        assertEquals("INSERT INTO TABLE (COLUMN1, COLUMN2) VALUES (?, ?)", builder.getQuery());
    }

    @Test
    void update() {
        builder.update("TABLE");
        assertEquals("UPDATE TABLE", builder.getQuery());
    }

    @Test
    void set() {
        builder.update("TABLE").set("COLUMN1", "VALUE1");
        assertEquals("UPDATE TABLE SET COLUMN1 = ?", builder.getQuery());
    }

    @Test
    void testSet() {

    }

    @Test
    void deleteFrom() {
        builder.deleteFrom("TABLE");
        assertEquals("DELETE FROM TABLE", builder.getQuery());
    }

    @Test
    void join() {
       builder.select().from("TABLE").join("TABLE2", "TABLE.COLUMN1 = TABLE2.COLUMN2");
        assertEquals("SELECT * FROM TABLE JOIN TABLE2 ON TABLE.COLUMN1 = TABLE2.COLUMN2", builder.getQuery());
    }

    @Test
    void leftJoin() {
        builder.select().from("TABLE").leftJoin("TABLE2", "TABLE.COLUMN1 = TABLE2.COLUMN2");
        assertEquals("SELECT * FROM TABLE LEFT JOIN TABLE2 ON TABLE.COLUMN1 = TABLE2.COLUMN2", builder.getQuery());
    }

    @Test
    void groupBy() {
        builder.select("COLUMN1", "COLUMN2").from("TABLE").groupBy("COLUMN1", "COLUMN2");
        assertEquals("SELECT COLUMN1, COLUMN2 FROM TABLE GROUP BY COLUMN1, COLUMN2", builder.getQuery());
    }

    @Test
    void having() {
        builder.select("COUNT(*)").from("TABLE").having("COLUMN1 = 1");
        assertEquals("SELECT COUNT(*) FROM TABLE HAVING COLUMN1 = 1", builder.getQuery());
    }

    @Test
    void from() {
        builder.select().from("TABLE");
        assertEquals("SELECT * FROM TABLE", builder.getQuery());
    }

    @Test
    void where() {
        builder.select().from("TABLE").where("COLUMN1 = 1");
        assertEquals("SELECT * FROM TABLE WHERE COLUMN1 = 1", builder.getQuery());
    }

    @Test
    void addParameter() {
        builder.addParameter(1);
        Object[] params = builder.getParameters();

        assertEquals(1, params[0]);
        assertEquals(1, params.length);
    }

    @Test
    void orderBy() {
        builder.select().from("TABLE").orderBy("COLUMN1");
        assertEquals("SELECT * FROM TABLE ORDER BY COLUMN1", builder.getQuery());
    }
}