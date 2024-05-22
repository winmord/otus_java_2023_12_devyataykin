package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";
    private static final String PARAM_CLIENT_NAME = "name";
    private static final String PARAM_CLIENT_ADDRESS = "address";
    private static final String PARAM_CLIENT_PHONE = "phone";
    private final DBServiceClient dbServiceClient;
    private final TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        List<Client> clients = dbServiceClient.findAll();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(TEMPLATE_ATTR_CLIENTS, clients);

        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
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
