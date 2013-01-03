package xxx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import xxx.StructuredParamRequestHandler;
import xxx.model.Employee;
import xxx.model.Address;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.RequestHandler;
import org.slim3.controller.validator.Validators;

public class SampleController extends Controller {

    @Override
    protected RequestHandler createRequestHandler(HttpServletRequest request) {
        StructuredParamRequestHandler handler = new StructuredParamRequestHandler(request);
        handler.addBeanClass("employees", Employee.class);
        handler.addBeanClass("employees.address", Address.class);
        return handler;
    }

    @Override
    public Navigation run() throws Exception {

        Validators v = new Validators(request);
        v.add("employees", v.required());

        if (!v.validate()) {
            return forward("sample_form.jsp");
        }

        List<Employee> employees = asList("employees");

        // do something.
    }

    @SuppressWarnings("unchecked")
    protected <T> SortedMap<Integer, T> asSortedMap(CharSequence name) {
        if (name == null) {
            throw new NullPointerException("The name parameter must not be null.");
        }
        return (SortedMap<Integer, T>) request.getAttribute(name.toString());
    }

    protected <T> List<T> asList(CharSequence name) {
        SortedMap<Integer, T> map = asSortedMap(name);
        return new ArrayList<T>(map.values());
    }

}
