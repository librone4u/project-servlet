package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);

        int index = getSelectedIndex(req);
        if(checkWin(resp, session, field)){
            return;
        }
        Sign currentSign = field.getField().get(index);

        if(Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        if(session.getAttribute("queue")  == Sign.CROSS) {
            field.getField().put(index, Sign.CROSS);
            if(checkWin(resp, session, field)){
                return;
            }
            else {
                session.setAttribute("queue", Sign.NOUGHT);
            }
        }
        else {
            field.getField().put(index, Sign.NOUGHT);
            if(checkWin(resp, session, field)){
                return;
            }
            else {
                session.setAttribute("queue", Sign.CROSS);
            }
        }
        if(field.getEmptyFieldIndex() == -1){
            session.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();

            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }
        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }
    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if(Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            session.setAttribute("data", data);
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession session) {
        Object fieldAttribute = session.getAttribute("field");
        if(Field.class != fieldAttribute.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
}
