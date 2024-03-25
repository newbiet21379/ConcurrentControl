package com.tim.transactioncase.diagram;

import net.sourceforge.plantuml.SourceStringReader;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.Files.*;

public class DiagramGenerator {
    public static void main(String[] args) throws IOException {
        String source =
                "@startuml\n"
                        + "title Order Service Diagram\n"

                        /* Class OrderService */
                        + "class OrderService{\n"
                        + "{field} final OrderRepository orderRepository\n"
                        + "{method} public Order createOrder(String orderInfo, List<String> details)\n"
                        + "{method} public Order save(Order order)\n"
                        + "{method} public Order createOrderFlow(String orderInfo, List<String> detailInfos)\n"
                        + "{method} public void updateOrderFlow(Long orderId, String newOrderInfo)\n"
                        + "{method} private Order generateOrder(String orderInfo, List<String> detailInfos)\n"
                        + "{method} private void updateOrder(Long orderId, String newOrderInfo)\n"
                        + "{method} Order findOrderById(Long orderId)\n"
                        + "{method} public void createAndUpdateOrder(String orderName, List<String> detailInfos, String newName)\n"
                        + "{method} public void processOrderBatchWithValidation(List<OrderRequest> orders, OrderValidator orderValidator)\n"
                        + "}\n"

                        /* Class Order */
                        + "class Order{\n"
                        + "{field} String orderInfo\n"
                        + "{field} List<OrderExecute> orderDetails\n"
                        + "}\n"

                        /* Class OrderExecute */
                        + "class OrderExecute{\n"
                        + "{field} String detailInfo\n"
                        + "{field} Order order\n"
                        + "}\n"

                        /* Class OrderRequest */
                        + "class OrderRequest{\n"
                        + "{field} String orderInfo\n"
                        + "{field} List<String> detailInfos\n"
                        + "}\n"

                        /* Class OrderValidator */
                        + "class OrderValidator{\n"
                        + "{method} boolean isValid(OrderRequest order)\n"
                        + "}\n"

                        /* Links between classes */
                        + "OrderService -- OrderRepository\n"
                        + "Order <.. OrderService\n"
                        + "Order --> OrderExecute : contains\n"
                        + "OrderService --> OrderRequest\n"
                        + "OrderService --> OrderValidator : uses\n"
                        + "@enduml";

        SourceStringReader reader = new SourceStringReader(source);
        String desc = reader.outputImage(newOutputStream(Paths.get("OrderService.png"))).getDescription();
        System.out.println(desc);
    }
}