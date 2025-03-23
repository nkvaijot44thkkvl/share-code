const stripe = Stripe('pk_test_51R5SWUBbUQcj3iTbu2M9FhIFYlBqQbIFgzx373zFo1GRLNvnJHa0BBLadCWR5fz6E6osDP9O1RzHOGfSNWPlidyB00ucw9VuYH');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});