<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<meta charset="utf-8">
<title>sample form</title>

<form action="sample" method="post">
  <table id="employees">
    <thead>
      <th>ID</th>
      <th>Name</th>
      <th>Address</th>
    </thead>
    <tbody>
<c:forEach var="emp" items="${employees}">
      <tr class="employee">
        <td>${emp.id}</td>
        <td><input type="text" name="employees[${emp.id}].name" value="${f:h(emp.name)}"></td>
        <td>
          <input type="text" name="employees[${emp.id}].address.zipCode" value="${f:h(emp.address.zipCode)}">
          <input type="text" name="employees[${emp.id}].address.line1" value="${f:h(emp.address.line1)}">
        </td>
      </tr>
</c:forEach>
    </tbody>
  </table>
  <input type="submit" value="ぜんぶ更新">
</form>
