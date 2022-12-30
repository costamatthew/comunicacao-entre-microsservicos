import Order from '../../modules/sales/model/Order.js';

export async function createInitialOrder() {
  await Order.collection.drop();

  await Order.create({
    products: [
      { productId: 1001, quantity: 2 },
      { productId: 1002, quantity: 1 },
      { productId: 1003, quantity: 1 },
    ],
    user: {
      id: 'askjdnalsjd',
      name: 'User Test',
      email: 'usertest@mail.com',
    },
    status: 'APPROVED',
    createAt: new Date(),
    updatedAt: new Date(),
  });

  await Order.create({
    products: [
      { productId: 1004, quantity: 2 },
      { productId: 1005, quantity: 1 },
    ],
    user: {
      id: 'askjdnalsjd',
      name: 'User Test2',
      email: 'usertest2@mail.com',
    },
    status: 'REJECTED',
    createAt: new Date(),
    updatedAt: new Date(),
  });

  let initialData = await Order.find();
  console.info(`Initial data was created: ${initialData}`);
}
