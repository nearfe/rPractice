package server.pvptemple.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.pvptemple.api.model.Command;
import server.pvptemple.api.repo.CommandRepository;
import server.pvptemple.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/{key}/log")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class LogController {

	@Autowired private CommandRepository commandRepository;

	@RequestMapping("/commands/{id}")
	public ResponseEntity<List<Command>> getCommands(@PathVariable(name = "key") String key,
													 @PathVariable(name = "id") int id) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		return new ResponseEntity<>(this.commandRepository.findByPlayerId(id), HttpStatus.OK);
	}

	@RequestMapping("/command/{id}")
	public void onCommandLog(HttpServletRequest request, @PathVariable("key") String key, @PathVariable("id") int id) {
		if (!Constants.validServerKey(key)) {
			return;
		}

		String string = request.getParameter("command");

		Command command = new Command();
		command.setCommand(string);
		command.setPlayerId(id);

		this.commandRepository.save(command);
	}

}
