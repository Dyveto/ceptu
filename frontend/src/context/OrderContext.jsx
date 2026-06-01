import React, { createContext, useReducer, useContext } from 'react';
import { orderReducer, initialOrderState } from '../reducers/orderReducer.js';

const OrderContext = createContext(null);

export const OrderProvider = ({ children }) => {
    const [orderState, dispatch] = useReducer(orderReducer, initialOrderState);

    const addItem = (product, quantity = 1) => {
        dispatch({
            type: 'ADD_ITEM',
            payload: { productId: product.id, name: product.name, price: product.price, quantity }
        });
    };

    const removeItem = (productId) => {
        dispatch({ type: 'REMOVE_ITEM', payload: productId });
    };

    const updateQuantity = (productId, quantity) => {
        dispatch({ type: 'UPDATE_QUANTITY', payload: { productId, quantity } });
    };

    const setCustomer = (customerId) => {
        dispatch({ type: 'SET_CUSTOMER', payload: customerId });
    };

    const clearOrder = () => {
        dispatch({ type: 'CLEAR_ORDER' });
    };

    return (
        <OrderContext.Provider value={{ orderState, addItem, removeItem, updateQuantity, setCustomer, clearOrder }}>
            {children}
        </OrderContext.Provider>
    );
};

export const useOrderContext = () => {
    const ctx = useContext(OrderContext);
    if (!ctx) throw new Error('useOrderContext debe usarse dentro de <OrderProvider>');
    return ctx;
};