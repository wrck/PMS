package com.dp.plat.lowcode.engine.microflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MicroflowEngineTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MicroflowEngine engine = new MicroflowEngine(objectMapper, List.of(
            new AssignExecutor(),
            new ConditionExecutor(),
            new ReturnExecutor(),
            new StartEndExecutor()));

    @Test
    void shouldExecuteSimpleAssignAndReturn() {
        String definition = "{\"nodes\":[" +
                "{\"id\":\"s\",\"type\":\"START\"}," +
                "{\"id\":\"a\",\"type\":\"ASSIGN\",\"config\":{\"target\":\"x\",\"expression\":\"1 + 2\"}}," +
                "{\"id\":\"r\",\"type\":\"RETURN\",\"config\":{\"expression\":\"x * 10\"}}," +
                "{\"id\":\"e\",\"type\":\"END\"}]," +
                "\"edges\":[" +
                "{\"source\":\"s\",\"target\":\"a\"}," +
                "{\"source\":\"a\",\"target\":\"r\"}," +
                "{\"source\":\"r\",\"target\":\"e\"}]}";

        MicroflowContext ctx = engine.execute(definition, Map.of());
        assertThat(ctx.getResult()).isEqualTo(30);
        assertThat(ctx.getVariable("x")).isEqualTo(3);
    }

    @Test
    void shouldFollowConditionTrueBranch() {
        String definition = "{\"nodes\":[" +
                "{\"id\":\"s\",\"type\":\"START\"}," +
                "{\"id\":\"a\",\"type\":\"ASSIGN\",\"config\":{\"target\":\"age\",\"expression\":\"18\"}}," +
                "{\"id\":\"c\",\"type\":\"CONDITION\",\"config\":{\"expression\":\"age >= 18\",\"trueBranch\":\"r1\",\"falseBranch\":\"r2\"}}," +
                "{\"id\":\"r1\",\"type\":\"RETURN\",\"config\":{\"expression\":\"'adult'\"}}," +
                "{\"id\":\"r2\",\"type\":\"RETURN\",\"config\":{\"expression\":\"'minor'\"}}]," +
                "\"edges\":[" +
                "{\"source\":\"s\",\"target\":\"a\"}," +
                "{\"source\":\"a\",\"target\":\"c\"}]}";

        MicroflowContext ctx = engine.execute(definition, Map.of());
        assertThat(ctx.getResult()).isEqualTo("adult");
    }

    @Test
    void shouldFollowConditionFalseBranch() {
        String definition = "{\"nodes\":[" +
                "{\"id\":\"s\",\"type\":\"START\"}," +
                "{\"id\":\"a\",\"type\":\"ASSIGN\",\"config\":{\"target\":\"age\",\"expression\":\"15\"}}," +
                "{\"id\":\"c\",\"type\":\"CONDITION\",\"config\":{\"expression\":\"age >= 18\",\"trueBranch\":\"r1\",\"falseBranch\":\"r2\"}}," +
                "{\"id\":\"r1\",\"type\":\"RETURN\",\"config\":{\"expression\":\"'adult'\"}}," +
                "{\"id\":\"r2\",\"type\":\"RETURN\",\"config\":{\"expression\":\"'minor'\"}}]," +
                "\"edges\":[" +
                "{\"source\":\"s\",\"target\":\"a\"}," +
                "{\"source\":\"a\",\"target\":\"c\"}]}";

        MicroflowContext ctx = engine.execute(definition, Map.of());
        assertThat(ctx.getResult()).isEqualTo("minor");
    }

    @Test
    void shouldUseInputVariables() {
        String definition = "{\"nodes\":[" +
                "{\"id\":\"s\",\"type\":\"START\"}," +
                "{\"id\":\"r\",\"type\":\"RETURN\",\"config\":{\"expression\":\"name + ' says hi'\"}}]," +
                "\"edges\":[{\"source\":\"s\",\"target\":\"r\"}]}";

        MicroflowContext ctx = engine.execute(definition, Map.of("name", "Alice"));
        assertThat(ctx.getResult()).isEqualTo("Alice says hi");
    }
}
