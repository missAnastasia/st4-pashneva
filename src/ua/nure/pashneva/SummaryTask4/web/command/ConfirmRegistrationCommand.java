package ua.nure.pashneva.SummaryTask4.web.command;

import org.apache.log4j.Logger;
import ua.nure.pashneva.SummaryTask4.db.dao.DAOFactory;
import ua.nure.pashneva.SummaryTask4.db.entity.User;
import ua.nure.pashneva.SummaryTask4.db.entity.UserStatus;
import ua.nure.pashneva.SummaryTask4.exception.AppException;
import ua.nure.pashneva.SummaryTask4.web.util.Path;
import ua.nure.pashneva.SummaryTask4.web.util.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

public class ConfirmRegistrationCommand extends Command {

    private static final Logger LOG = Logger.getLogger(ConfirmRegistrationCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, AppException {
        LOG.debug("Command starts");
        HttpSession session = request.getSession();
        User userToConfirm = (User) session.getAttribute("userToConfirm");
        String login = request.getParameter("login");
        if (userToConfirm == null ||
                login == null || login.isEmpty() || !login.equals(userToConfirm.getLogin())) {
            String message = ResourceBundle.getBundle("resources", request.getLocale())
                    .getString("message.error.failed_to_confirm_registration");
            throw new AppException(message);
        } else {
            userToConfirm.setUserStatus(UserStatus.UNBLOCKED);
            try {
                DAOFactory.getInstance().getUserDAO().create(userToConfirm);
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
        }
        session.invalidate();
        LOG.debug("Command finished");
        response.sendRedirect(Path.COMMAND_MESSAGE_SUCCESS +
                ResourceBundle.getBundle("resources", request.getLocale())
                        .getString("message.success.success_registration"));
    }
}
