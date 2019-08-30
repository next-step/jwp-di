package next.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.annotation.Inject;
import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import core.mvc.JsonView;
import core.mvc.ModelAndView;
import next.dto.UserCreatedDto;
import next.dto.UserUpdatedDto;
import next.model.User;
import next.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ApiUserController {

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    @Inject
    public ApiUserController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public ModelAndView create(final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        final UserCreatedDto createdDto = objectMapper.readValue(request.getInputStream(), UserCreatedDto.class);
        logger.debug("Created User : {}", createdDto);

        final User user = new User(createdDto.getUserId(), createdDto.getPassword(), createdDto.getName(),
                createdDto.getEmail());
        userService.insert(user);

        response.setHeader("Location", "/api/users?userId=" + createdDto.getUserId());
        response.setStatus(HttpStatus.CREATED.value());

        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ModelAndView show(final HttpServletRequest request,
                             final HttpServletResponse response) throws Exception {
        final String userId = request.getParameter("userId");
        logger.debug("userId : {}", userId);

        final User user = userService.findByUserId(userId);

        final ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.PUT)
    public ModelAndView update(final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        final String userId = request.getParameter("userId");
        logger.debug("userId : {}", userId);

        final UserUpdatedDto updateDto = objectMapper.readValue(request.getInputStream(), UserUpdatedDto.class);
        logger.debug("Updated User : {}", updateDto);

        userService.update(userId, updateDto);

        return new ModelAndView(new JsonView());
    }
}
