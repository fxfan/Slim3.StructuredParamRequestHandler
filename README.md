Slim3.StructuredParamRequestHandler
===================================

    employees[0].name
    employees[0].address.zipCode
    employees[0].address.line1
    employees[1].name
    employees[1].address.zipCode
    employees[1].address.line1
    ...

こういう感じのパラメータ名を受け取ると、

    SortedMap<Integer, Employee> employeeMap = new SortedMap<Integer, Employee>();
    
    Address addr0 = new Address();
    addr0.setZipCode(asString("employees[0].address.zipCode"));
    addr0.setLine1(asString("employees[0].address.line1"));
    Employee emp0 = new Employee();
    emp0.setName(asString("employees[0].name"));
    emp0.setAddress(addr0);
    employeeMap.put(0, emp0);
    
    Address addr1 = new Address();
    addr1.setZipCode(asString("employees[1].address.zipCode"));
    addr1.setLine1(asString("employees[1].address.line1"));
    Employee emp1 = new Employee();
    emp1.setName(asString("employees[1].name"));
    emp1.setAddress(addr1);
    employeeMap.put(1, emp1);
    
みたいなことを勝手にやってくれて、Controller側では

    SortedMap<Integer, Employee> employeeMap = asSortedMap("employees");

とか

    List<Employee> employees = asList("employees");

などと書くだけでパラメータが取得できるかもしれない。できるといいな。

