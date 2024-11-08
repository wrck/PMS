package code;

import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonJsonTest {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        DataTableColumn dataFieldRelation = new DataFieldRelation();
        dataFieldRelation.setTitle("");
        dataFieldRelation.setData("xxxxxxxxxx");
        try {
            System.out.println(mapper.writeValueAsString(dataFieldRelation));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
