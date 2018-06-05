/* global fetch */

if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                    ? args[number]
                    : match
                    ;
        });
    };
}

const config_local = {
    http_server: "http://localhost:8083",
    ws_server: "ws://localhost:8083"
};
const config_cloudfoundry = {
    http_server: "https://tjonahen-diner.cfapps.io",
    ws_server: "wss://tjonahen-diner.cfapps.io:4443"
};

class Config {
    orderstatus = "{0}/orderstatus";
    menu = "{0}/api/menu";
    bill = "{0}/api/order/{1}/bill";
    pay = "{0}/api/order/{1}/pay";
    ordersubmit = "{0}/api/order";
    table = "{0}/api/table/reserve";

    constructor() {
        this.config = config_local;
    }
    orderstatus() {
        return this.orderstatus.format(this.config.ws_server);
    }
    bill(ref) {
        return this.bill.format(this.config.http_server, ref);
    }
    table() {
        return this.table.format(this.config.http_server);
    }
    menu() {
        return this.menu.format(this.config.http_server);
    }
    pay(ref) {
        return this.pay.format(this.config.http_server, ref);
    }
    ordersubmit() {
        return this.ordersubmit.format(this.config.http_server);
    }
}

const config = new Config();

class OrderWebSocket {
    constructor(onmesg) {
        this.ws = new WebSocket(config.orderstatus());
        this.ws.onopen = () => {
            console.log("Connection is open...");
        };

        this.ws.onmessage = onmesg;

        this.ws.onclose = () => {
            // websocket is closed.
            console.log("Connection is closed...");
        };
    }
    sendOrderId(id) {
        this.ws.send(JSON.stringify({'orderid': id}));
    }
}

class IndexView {
    constructor() {
        this.eatContainer = document.querySelector("#Eat");
        this.drinksContainer = document.querySelector("#Drinks");
        this.orderContainer = document.querySelector("#Basket");
        this.statusContainer = document.querySelector("#Status");
        this.drinks = [];
        this.dishes = [];
        this.orderInfo = undefined;
        this.served = false;
    }

    init() {
        this.ws = new OrderWebSocket((evt) => {
            var status = evt.data;
            if (this.served) {
                status = "BILLING";
            }
            switch (status) {
                case "BILLING":
                    this.clearStatusContainer();
                    fetch(config.bill(this.orderInfo.ref)).then(res => res.json()).then(bill => {
                        const table = document.createElement("table");
                        table.className = "order-table";
                        bill.items.forEach((c) => {
                            table.appendChild(this.appendBillRow(c.name, c.quantity, c.price * c.quantity));
                        });
                        table.appendChild(this.appendBillRow("Total", null, bill.total));

                        this.statusContainer.appendChild(table);
                        this.statusContainer.appendChild(this.payBillButton());

                    });
                    break;
                case "DRINK_SERVED":
                    this.served = true;
                    this.statusContainer.appendChild(this.newH5("Drinks are served.."));
                    break;
                case "FOOD_SERVED":
                    this.served = true;
                    this.statusContainer.appendChild(this.newH5("Food is served.."));
                    break;
            }
        });
        fetch(config.table()).then(res => res.json()).then(table => {
            this.table_id = table.id;
        });
        fetch(config.menu()).then(res => {
            var xCsrfToken = res.headers.get("X-CSRF-TOKEN");
            console.log(res.headers);
            return res.json();
        }).then(menu => {
            menu.dishes.forEach((d) => {
                this.eatContainer.appendChild(this.newH5(d.name));
                this.eatContainer.appendChild(this.newDescription(d));
                this.eatContainer.appendChild(this.anchorAddToFoodCart(d));

            });
            menu.drinks.forEach((d) => {
                this.drinksContainer.appendChild(this.newH5(d.name));
                this.drinksContainer.appendChild(this.newDescription(d));
                this.drinksContainer.appendChild(this.anchorddToDrinkCart(d));
            });
        }
        );

    }

    clearStatusContainer() {
        while (this.statusContainer.firstChild)
            this.statusContainer.removeChild(this.statusContainer.firstChild);
    }
    appendBillRow(name, count, price) {
        const row = document.createElement("tr");
        var cell = document.createElement("td");
        cell.className = "first-cell";
        cell.appendChild(this.newH5(name));
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "second-cell";
        if (count) {
            cell.appendChild(this.newH5(count));
        }
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "third-cell";
        cell.appendChild(this.newH5(this.markupPrice("" + price)));
        row.appendChild(cell);
        return row;

    }

    payBillButton() {
        const p = document.createElement("p");
        const button = document.createElement("button");
        button.className = "w3-button";
        button.type = "submit";
        button.onclick = (e) => {

            fetch(config.pay(this.orderInfo.ref),
                    {
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        method: "POST"
                    })
                    .then(res => {
                        document.getElementById('orderstatus').style.display = 'none';
                        this.clearStatusContainer();
                        document.getElementById('thankyou').style.display = 'block';
                        setTimeout(function () {
                            document.getElementById('thankyou').style.display = 'none';
                        }, 2000);
                    })
                    .catch(res => {
                        console.log(res);
                    });

        };
        button.appendChild(document.createTextNode("PAY BILL"));
        p.appendChild(button);
        return p;


    }

    newH5(text) {
        const h = document.createElement("h5");
        h.appendChild(document.createTextNode(text));
        return h;
    }

    newDescription(d) {
        const p = document.createElement("p");
        p.className = "w3-text-grey";
        const price = "" + d.price;
        p.appendChild(document.createTextNode(d.description + " " + this.markupPrice(price)));

        return p;
    }

    markupPrice(price) {
        return price.slice(0, price.length - 2) + "." + price.slice(price.length - 2, price.length);
    }

    anchorAddToFoodCart(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            const current = this.dishes.find(e => {
                return e.d.ref === d.ref;
            });
            if (current) {
                current.count++;
            } else {
                this.dishes.push({'d': d, 'count': 1});
            }
        };
        const it = document.createElement("i");
        it.className = "w3-ripple w3-xxlarge fa fa-cart-plus";
        a.appendChild(it);
        return a;
    }

    anchorddToDrinkCart(d) {

        const a = document.createElement("a");
        a.className = "addToBasket";
        a.onclick = (e) => {
            const current = this.drinks.find(e => {
                return e.d.ref === d.ref;
            });
            if (current) {
                current.count++;
            } else {
                this.drinks.push({'d': d, 'count': 1});
            }
        };
        const it = document.createElement("i");
        it.className = "w3-ripple w3-xxlarge fa fa-cart-plus";
        a.appendChild(it);
        return a;
    }
    anchorRemoveFromFoodCart(c) {

        const a = document.createElement("a");
        a.onclick = (e) => {
            c.count--;
            if (c.count <= 0) {
                this.dishes = this.dishes.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode(c.count + " : "));
        const it = document.createElement("i");
        it.className = "w3-ripple w3-xxlarge fa fa-trash";
        a.appendChild(it);
        return a;
    }
    anchorRemoveFromDrinkCart(c) {

        const a = document.createElement("a");
        a.onclick = (e) => {
            c.count--;
            if (c.count <= 0) {
                this.drinks = this.drinks.filter(cc => {
                    return cc.d.ref !== c.d.ref;
                });
            }
            this.fillBasket();
        };
        a.appendChild(document.createTextNode(c.count + " : "));
        const it = document.createElement("i");
        it.className = "w3-ripple w3-xxlarge fa fa-trash";
        a.appendChild(it);
        return a;
    }

    fillBasket() {
        this.clearOrderContainer();
        if (this.dishes.length > 0 || this.drinks.length > 0) {
            const table = document.createElement("table");
            table.className = "order-table";
            var total = 0;
            this.dishes.forEach((c) => {
                table.appendChild(this.appendOrderRow(c.d.name, c.d.price * c.count, this.anchorRemoveFromFoodCart(c)));
                total += c.d.price * c.count;
            });
            this.drinks.forEach((c) => {
                table.appendChild(this.appendOrderRow(c.d.name, c.d.price * c.count, this.anchorRemoveFromDrinkCart(c)));
                total += c.d.price * c.count;
            });
            table.appendChild(this.appendOrderRow("Total", total, null));

            this.orderContainer.appendChild(table);
            this.orderContainer.appendChild(this.sendOrderButton());
        } else {
            this.orderContainer.appendChild(this.newH5("Cart is empty. Add food or dinks to the card.."));
        }
    }
    sendOrderButton() {
        const p = document.createElement("p");
        const button = document.createElement("button");
        button.className = "w3-button";
        button.type = "submit";
        button.onclick = (e) => {

            const order = [];
            this.dishes.forEach((c) => {
                order.push({"ref": c.d.ref, "quantity": c.count, "type": "DISH"});
            });
            this.drinks.forEach((c) => {
                order.push({"ref": c.d.ref, "quantity": c.count, "type": "DRINK"});
            });
            fetch(config.ordersubmit(),
                    {
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        method: "POST",
                        body: JSON.stringify(order)
                    })
                    .then(orderinfo => orderinfo.json())
                    .then(orderinfo => {
                        console.log(orderinfo);
                        this.orderInfo = orderinfo;
                        this.closeCart();
                        this.dishes = [];
                        this.drinks = [];
                        document.getElementById('orderstatus').style.display = 'block';
                        this.clearStatusContainer();
                        this.statusContainer.appendChild(this.newSpinner());
                        this.ws.sendOrderId(orderinfo.ref);
                    })
                    .catch(res => {
                        console.log(res);
                    });

        };
        button.appendChild(document.createTextNode("SEND ORDER"));
        p.appendChild(button);
        return p;
    }

    newSpinner() {
        const p = document.createElement("div");
        p.className = "loader";
        return p;
    }

    appendOrderRow(name, price, c) {
        const row = document.createElement("tr");
        var cell = document.createElement("td");
        cell.className = "first-cell";
        cell.appendChild(this.newH5(name));
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "second-cell";
        cell.appendChild(this.newH5(this.markupPrice("" + price)));
        row.appendChild(cell);
        cell = document.createElement("td");
        cell.className = "third-cell";
        if (c) {
            cell.appendChild(c);
        }
        row.appendChild(cell);
        return row;

    }
    openCart() {
        this.fillBasket();
        document.getElementById('order').style.display = 'block';
    }
    closeCart() {
        document.getElementById('order').style.display = 'none';
    }
    clearOrderContainer() {
        while (this.orderContainer.firstChild)
            this.orderContainer.removeChild(this.orderContainer.firstChild);
    }

}

const view = new IndexView();
view.init();



