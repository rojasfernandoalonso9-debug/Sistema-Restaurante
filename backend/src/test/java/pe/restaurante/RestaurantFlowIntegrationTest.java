package pe.restaurante;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestaurantFlowIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void allowsDevelopmentAndPreviewOrigins() throws Exception {
        for (String origin : new String[]{"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:4173", "http://127.0.0.1:4173"}) {
            mvc.perform(options("/api/health").header("Origin", origin).header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", origin));
        }
    }

    @Test void authenticatesCreatesOrderDeliversAndChargesOnce() throws Exception {
        mvc.perform(post("/api/auth/setup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Admin de prueba","email":"admin@test.local","password":"una-clave-segura-de-prueba"}
                    """)).andExpect(status().isOk());
        var login=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"admin@test.local","password":"una-clave-segura-de-prueba"}
                    """)).andExpect(status().isOk()).andReturn();
        JsonNode session=mapper.readTree(login.getResponse().getContentAsString());
        String token=session.get("token").asText();

        var category=mvc.perform(post("/api/categories").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Comidas","description":"Menú de prueba"}
                    """)).andExpect(status().isCreated()).andReturn();
        long categoryId=mapper.readTree(category.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(post("/api/employees").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"fullName":"Personal de prueba","jobTitle":"Mesero","phone":"5550101"}
                    """)).andExpect(status().isCreated()).andExpect(jsonPath("$.jobTitle").value("Mesero"));
        var table=mvc.perform(post("/api/tables").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tableNumber":"T-01","capacity":4,"status":"AVAILABLE"}
                    """)).andExpect(status().isCreated()).andReturn();
        long tableId=mapper.readTree(table.getResponse().getContentAsString()).get("id").asLong();
        var product=mvc.perform(post("/api/products").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Menú de prueba","description":"Plato", "price":12.50,"categoryId":%d,"available":true}
                    """.formatted(categoryId))).andExpect(status().isCreated()).andReturn();
        long productId=mapper.readTree(product.getResponse().getContentAsString()).get("id").asLong();
        var inventory=mvc.perform(post("/api/inventory").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Arroz prueba","unit":"kg","minimumQuantity":2}
                    """)).andExpect(status().isCreated()).andReturn();
        long inventoryId=mapper.readTree(inventory.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(post("/api/inventory/{id}/movements",inventoryId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"movementType":"IN","quantity":4,"note":"Ingreso de prueba"}
                    """)).andExpect(status().isCreated()).andExpect(jsonPath("$.type").value("IN"));
        mvc.perform(post("/api/inventory/{id}/movements",inventoryId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"movementType":"OUT","quantity":5,"note":"No debe permitirse"}
                    """)).andExpect(status().isConflict());
        mvc.perform(get("/api/inventory/{id}/movements",inventoryId).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantity").value(4.0));
        mvc.perform(put("/api/products/{id}/recipe",productId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"ingredients":[{"inventoryItemId":%d,"quantityPerProduct":1.25}]}
                    """.formatted(inventoryId)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].unit").value("kg"));

        var order=mvc.perform(post("/api/orders").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tableId":%d,"items":[{"productId":%d,"quantity":2}]}
                    """.formatted(tableId,productId))).andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(25.0)).andReturn();
        long orderId=mapper.readTree(order.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(patch("/api/orders/{id}/status",orderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"PREPARING\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/orders/{id}/status",orderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"READY\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/orders/{id}/status",orderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DELIVERED\"}")).andExpect(status().isOk());
        mvc.perform(get("/api/inventory/{id}/movements",inventoryId).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].type").value("OUT")).andExpect(jsonPath("$[0].quantity").value(2.5));
        var stockLimitedOrder=mvc.perform(post("/api/orders").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"tableId\":null,\"items\":[{\"productId\":"+productId+",\"quantity\":2}]}")).andExpect(status().isCreated()).andReturn();
        long stockLimitedId=mapper.readTree(stockLimitedOrder.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(patch("/api/orders/{id}/status",stockLimitedId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"PREPARING\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/orders/{id}/status",stockLimitedId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"READY\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/orders/{id}/status",stockLimitedId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DELIVERED\"}")).andExpect(status().isConflict());
        mvc.perform(get("/api/inventory").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].quantity").value(1.5));
        var sale=mvc.perform(post("/api/sales/orders/{id}/checkout",orderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"paymentMethod\":\"CASH\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(25.0)).andReturn();
        long saleId=mapper.readTree(sale.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(post("/api/sales/orders/{id}/checkout",orderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"paymentMethod\":\"CASH\"}")).andExpect(status().isConflict());
        mvc.perform(get("/api/dashboard/metrics").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.salesCount").value(1)).andExpect(jsonPath("$.recentOrders[1].tableName").value("T-01"));
        mvc.perform(get("/api/tables").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("AVAILABLE"));
        mvc.perform(get("/api/reports/summary").param("period","DAILY").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.completedSales").value(1)).andExpect(jsonPath("$.revenue").value(25.0)).andExpect(jsonPath("$.bestSellingProducts[0].quantity").value(2));
        mvc.perform(post("/api/sales/{id}/void",saleId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"Corrección de prueba\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("VOIDED"));
        mvc.perform(get("/api/reports/summary").param("period","DAILY").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.completedSales").value(0)).andExpect(jsonPath("$.revenue").value(0.0));
        var reservedTable=mvc.perform(post("/api/tables").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"tableNumber\":\"T-02\",\"capacity\":2,\"status\":\"RESERVED\"}")).andExpect(status().isCreated()).andReturn();
        long reservedTableId=mapper.readTree(reservedTable.getResponse().getContentAsString()).get("id").asLong();
        var cancelledOrder=mvc.perform(post("/api/orders").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"tableId\":"+reservedTableId+",\"items\":[{\"productId\":"+productId+",\"quantity\":1}]}")).andExpect(status().isCreated()).andReturn();
        long cancelledOrderId=mapper.readTree(cancelledOrder.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(patch("/api/orders/{id}/status",cancelledOrderId).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CANCELLED\"}")).andExpect(status().isOk());
        mvc.perform(get("/api/tables").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[1].status").value("RESERVED"));
        mvc.perform(post("/api/users").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Encargado de prueba","email":"manager@test.local","password":"otra-clave-segura-de-prueba","role":"MANAGER"}
                    """)).andExpect(status().isCreated());
        var managerLogin=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                    {"email":"manager@test.local","password":"otra-clave-segura-de-prueba"}
                    """)).andExpect(status().isOk()).andReturn();
        String managerToken=mapper.readTree(managerLogin.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(get("/api/users").header("Authorization","Bearer "+managerToken)).andExpect(status().isForbidden());
        mvc.perform(post("/api/sales/{id}/void",saleId).header("Authorization","Bearer "+managerToken).contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"No autorizado\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/users").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content("""
                    {"name":"Personal de catálogo","email":"staff@test.local","password":"clave-de-prueba-staff","role":"STAFF"}
                    """)).andExpect(status().isCreated());
        var staffLogin=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                    {"email":"staff@test.local","password":"clave-de-prueba-staff"}
                    """)).andExpect(status().isOk()).andReturn();
        String staffToken=mapper.readTree(staffLogin.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(get("/api/products").header("Authorization","Bearer "+staffToken)).andExpect(status().isOk()).andExpect(jsonPath("$[0].categoryName").value("Comidas"));
        mvc.perform(get("/api/categories").header("Authorization","Bearer "+staffToken)).andExpect(status().isOk());
        mvc.perform(post("/api/categories").header("Authorization","Bearer "+staffToken).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"No autorizada\"}")).andExpect(status().isForbidden());
    }
}
