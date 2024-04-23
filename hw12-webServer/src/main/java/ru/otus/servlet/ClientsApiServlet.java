package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;

public class ClientsApiServlet extends HttpServlet {
    private static final String PARAM_CLIENT_NAME = "name";
    private static final String PARAM_CLIENT_ADDRESS = "address";
    private static final String PARAM_CLIENT_PHONE = "phone";
    private final DBServiceClient dbServiceClient;

    public ClientsApiServlet(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Client client = extractClientFromRequest(req);
        dbServiceClient.saveClient(client);
        resp.sendRedirect("/clients");
    }

    private Client extractClientFromRequest(HttpServletRequest req) {
        String name = req.getParameter(PARAM_CLIENT_NAME);
        String street = req.getParameter(PARAM_CLIENT_ADDRESS);
        String number = req.getParameter(PARAM_CLIENT_PHONE);

        Address address = new Address(null, street);
        List<Phone> phones = new ArrayList<>();

        if (!number.isEmpty() && !number.isBlank()) {
            phones.add(new Phone(null, number));
        }

        return new Client(null, name, address, phones);
    }
}
