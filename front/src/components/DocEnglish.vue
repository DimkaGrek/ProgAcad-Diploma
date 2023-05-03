<template>
  <div class="ff-500-18 text">
    <p>
      This is a web application that allows you to keep track of electricity
      meters in company.
    </p>
    <!-- <p>
       When you enter the site, a window pops up to enter your username and password. For
         access to the application, you must be a registered user.
         The administrator can register you in the application.
      </p>
      <p><img src="../assets/images/doc/login.png" alt="login window" /></p> -->

    <h1>Counters page</h1>
    <p>
      When you open the site, you get to a page that displays a table with
      counters. Also when you click on the button
      <button class="btn-menu">Counters</button> in the top menu, also this page
      opens
    </p>
    <p>
      With button
      <button class="btn-add">+ Add counter</button> opens the form adding a new
      counter.
    </p>
    <h2>Form for adding/editing a new counter</h2>
    <ul>
      <li>
        <span class="label">Pavilion number</span> - the pavilion where the
        counter
      </li>
      <li>
        <span class="label">Place#</span> - a kiosk or a place in a pavilion
        where the counter is located
      </li>
      <li>
        <span class="label">Counter type</span> - in this field you need to
        specify, mechanical or digital type, or other type of counter
      </li>
      <li><span class="label">Counter No.</span> - counter serial number.</li>
      <li>
        <span class="label">Counter capacity</span> - number of digits meter
        reading
      </li>
      <li>
        <span class="label">Date of manufacture</span> - when it was made
        counter. Indicated on the counter
      </li>
      <li>
        <span class="label">Installation date</span> - when the counter was
        installed into the room
      </li>
      <li>
        <span class="label">Tenant</span> - you must select the current tenant
        who rents the premises
      </li>
      <li>
        <span class="label">Parent counter number</span> - here it is necessary
        enter the number of the counter through which this counter is connected
      </li>
      <li>
        <span class="label">Additional information</span> - in this field you
        can enter any text.
      </li>
    </ul>
    <h2>Editing the counter</h2>
    <p>
      When hovering over the field "Counter No." in the drop-down menu, you can
      select "<span class="brown">Edit</span>" and in the edit window make
      changes to the counter
    </p>
    <h2>Remove counter</h2>
    <p>
      When hovering over the field "Counter No." in the drop-down menu, you can
      select "<span class="red">Delete</span>". The counter is not removed from
      the database, but is marked as deleted. You can show all remote counters
      by clicking on the link in the upper field "Inactive".
    </p>
    <h2>Accruals by counter</h2>
    <p>
      By clicking on the counter number, or by selecting "Accruals" in the
      drop-down menu, the table of accruals for the selected counter is
      displayed. With button
      <button class="btn-add">+ Add Accrual</button> a window opens, where you
      can enter your billing statement.
    </p>
    <ul>
      <li>
        <span class="label">For the month</span> - select the month for which
        accruals are made. <span class="label">year</span> - select a year, for
        which accruals are made
      </li>
      <li>
        <span class="label">Current readings</span> - enter the current
        readings, which have been taken.
      </li>
      <li>
        <span class="label">Previous readings</span> - enter the latest previous
        readings.
      </li>
      <li>
        <span class="label">Tariff</span> - enter the tariff for which calculate
        the payment amount.
      </li>
      <li>
        <span class="label">Date</span> - enter the date when you enter the data
        to the database (by default, the current date and time are set).
      </li>
      <li>
        <span class="label">Additional information</span> - in this field you
        can enter any text.
      </li>
    </ul>
    <h2>Editing accruals</h2>
    <p>
      When you click on the Period (Month) field, an editing window opens. You
      can only edit UNPAID charges
    </p>
    <h2>Payment of accruals (receipts)</h2>
    <p>
      By clicking on the button "<span class="needpay">Payment</span> ", a
      window opens, where you can fill out payment information:
    </p>
    <ul>
      <li>
        <span class="label">Payment receipt number</span> - enter the receipt
        number, which gives out your cash register
      </li>
      <li>
        <span class="label">Payment amount</span> - enter the amount that the
        tenant pays for this receipt. The default is full payment amount of this
        accrual.
      </li>
      <li>
        <span class="label">Date</span> - the date the payment was made. It puts
        the current date and time default.
      </li>
      <li>
        <span class="label">Additional information</span> - in this field you
        can enter any text.
      </li>
    </ul>
    <h2>Mass payment of accruals (receipts)</h2>
    <p>
      By selecting UNpaid receipts using the checkbox, and clicking in the
      left-side menu on icon
      <img src="../components/icons/money.svg" alt="payment" width="25" />
      "Pay", a window opens where you can enter the amount of payment for
      several selected accruals and pay with one check.
    </p>
    <h2>Print Notifications on accruals</h2>
    <p>
      By selecting UNpaid accruals (receipts) using the checkbox and clicking on
      side menu button
      <img src="../components/icons/printer.svg" alt="printer" width="25" />
      "Print", in another tab opens Notifications for selected accruals
      (receipts) in .pdf format, which can then be saved or print
    </p>
    <h2>Deleting accruals (receipts)</h2>
    <p>
      By selecting the UNPAID accruals (receipts) using the checkbox and
      clicking on side menu button
      <img src="../components/icons/ben.svg" alt="ben" width="25" />
      "Delete", the selected accruals (receipts) will be deleted
    </p>
    <h2>Payment receipt</h2>
    <p>
      If the accrual is paid or partially paid, then by clicking on any field
      accrual, a window will open where there will be data on the payment (s)
      for this accrual
    </p>

    <h1>Tenants page</h1>
    <p>
      With button
      <button class="btn-menu">Tenants</button> "Tenants" in the top menu, the
      page with the table of tenants opens.
    </p>
    <p>
      With button
      <button class="btn-add">+ Add tenant</button> opens the form adding a new
      tenant.
    </p>
    <h2>Form for adding a new tenant</h2>
    <ul>
      <li><span class="label">Name</span> - enter the name of the tenant</li>
      <li>
        <span class="label">LastName</span> - enter the tenant's last name
      </li>
      <li>
        <span class="label">Certificate</span> - enter a certificate tenant
        registration
      </li>
      <li><span class="label">Passport</span> - enter the tenant's passport</li>
      <li><span class="label">Address</span> - enter tenant's address</li>
      <li>
        <span class="label">Company form</span> - enter the company form tenant.
        For example Entepreneur or Legal entity
      </li>
      <li>
        <span class="label">Phone 1</span> - enter the tenant's first phone
      </li>
      <li>
        <span class="label">Phone 2</span> - enter the tenant's second phone
        number
      </li>
      <li><span class="label">E-mail</span> - enter tenant's e-mail</li>
      <li>
        <span class="label">Additional information</span> - in this field you
        can enter any text.
      </li>
    </ul>
    <h2>Editing a Tenant</h2>
    <p>
      In the "Actions" field, by clicking on the icon
      <img src="../components/icons/edit.svg" alt="Edit" width="25" />
      "Editing", the form for editing the tenant will open
    </p>

    <h1>Search form</h1>
    <p>
      Each page has a search form above the table. Possibility to filter table
      data for any field.
    </p>
  </div>
</template>

<style scoped>
ul li {
  margin-bottom: 10px;
}
.text {
  line-height: 1.5;
}
.label {
  border: 1px solid black;
  border-radius: 6px;
  padding: 3px;
}
</style>
