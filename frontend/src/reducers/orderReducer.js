export const initialOrderState = {
  items: [],
  customerId: '',
  total: 0
};

export function orderReducer(state, action) {
  switch (action.type) {
    case 'ADD_ITEM': {
      const existingItemIndex = state.items.findIndex(item => item.productId === action.payload.productId);
      let newItems = [...state.items];

      if (existingItemIndex > -1) {
        newItems[existingItemIndex].quantity += action.payload.quantity || 1;
      } else {
        newItems.push(action.payload);
      }

      const newTotal = newItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      return { ...state, items: newItems, total: newTotal };
    }

    case 'REMOVE_ITEM': {
      const newItems = state.items.filter(item => item.productId !== action.payload);
      const newTotal = newItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      return { ...state, items: newItems, total: newTotal };
    }

    case 'UPDATE_QUANTITY': {
      const newItems = state.items.map(item => {
        if (item.productId === action.payload.productId) {
          return { ...item, quantity: action.payload.quantity };
        }
        return item;
      });
      const newTotal = newItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      return { ...state, items: newItems, total: newTotal };
    }

    case 'SET_CUSTOMER':
      return { ...state, customerId: action.payload };

    case 'CLEAR_ORDER':
      return initialOrderState;

    default:
      return state;
    }
}