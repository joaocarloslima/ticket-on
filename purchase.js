import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 20,
    iterations: 20,
    delay: '2s'
}

export default function () {
    let url = 'http://localhost:8080/tickets/purchase/1';
    let response = http.post(url);

    check(response, {
        'compra realizada com sucesso': (r) => r.status === 200,
    });
}
