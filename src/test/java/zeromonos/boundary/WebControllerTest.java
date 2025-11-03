package zeromonos.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void clientPage_ShouldReturnClientView() throws Exception {
        mvc.perform(get("/client"))
                .andExpect(status().isOk())
                .andExpect(view().name("client"))
                .andReturn();
    }

    @Test
    void employeePage_ShouldReturnEmployeeView() throws Exception {
        mvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee"))
                .andReturn();
    }
}
