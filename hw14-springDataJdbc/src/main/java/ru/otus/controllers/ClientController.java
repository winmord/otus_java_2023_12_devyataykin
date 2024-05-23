package ru.otus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.dto.ClientDto;
import ru.otus.mapper.ClientMapper;
import ru.otus.service.DBServiceClient;

@Controller
public class ClientController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private final DBServiceClient clientService;

    public ClientController(DBServiceClient clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/client")
    public RedirectView save(ClientDto clientDto) {
        log.info("{}", clientDto);
        clientService.saveClient(ClientMapper.toClient(clientDto));

        return new RedirectView("/");
    }

    @GetMapping("/")
    public String getAll(Model model) {
        model.addAttribute("client", new ClientDto());
        model.addAttribute("clients", clientService.findAll());

        return "clients";
    }
}
