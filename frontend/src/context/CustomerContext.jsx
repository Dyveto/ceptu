import React, { createContext, useReducer, useContext } from 'react';

const CustomerContext = createContext(null);

const initialCustomerState = {
    customers: [],
    loading: false,
    error: '',
    formSuccess: '',
    formError: '',
    showCustomerForm: false,
    editingCustomer: null,
    selectedCustomer: null,
    addresses: [],
    loadingAddresses: false,
    showAddressForm: false,
};

function customerReducer(state, action) {
    switch (action.type) {
        case 'SET_LOADING':
            return { ...state, loading: action.payload };
        case 'SET_ERROR':
            return { ...state, error: action.payload };
        case 'SET_CUSTOMERS':
            return { ...state, customers: action.payload };
        case 'SET_FORM_SUCCESS':
            return { ...state, formSuccess: action.payload, formError: '' };
        case 'SET_FORM_ERROR':
            return { ...state, formError: action.payload, formSuccess: '' };
        case 'CLEAR_FORM_MESSAGES':
            return { ...state, formSuccess: '', formError: '' };
        case 'TOGGLE_CUSTOMER_FORM':
            return { ...state, showCustomerForm: !state.showCustomerForm, editingCustomer: null, formSuccess: '', formError: '' };
        case 'SET_EDITING_CUSTOMER':
            return { ...state, editingCustomer: action.payload, showCustomerForm: false, formSuccess: '', formError: '' };
        case 'CLOSE_EDITING_CUSTOMER':
            return { ...state, editingCustomer: null };
        case 'SET_SELECTED_CUSTOMER':
            return { ...state, selectedCustomer: action.payload, addresses: [], showAddressForm: false };
        case 'CLOSE_ADDRESS_PANEL':
            return { ...state, selectedCustomer: null, addresses: [], showAddressForm: false };
        case 'SET_ADDRESSES':
            return { ...state, addresses: action.payload };
        case 'SET_LOADING_ADDRESSES':
            return { ...state, loadingAddresses: action.payload };
        case 'TOGGLE_ADDRESS_FORM':
            return { ...state, showAddressForm: !state.showAddressForm, formSuccess: '', formError: '' };
        default:
            return state;
    }
}

export const CustomerProvider = ({ children }) => {
    const [customerState, dispatch] = useReducer(customerReducer, initialCustomerState);

    const setLoading = (val) => dispatch({ type: 'SET_LOADING', payload: val });
    const setError = (msg) => dispatch({ type: 'SET_ERROR', payload: msg });
    const setCustomers = (data) => dispatch({ type: 'SET_CUSTOMERS', payload: data });
    const setFormSuccess = (msg) => dispatch({ type: 'SET_FORM_SUCCESS', payload: msg });
    const setFormError = (msg) => dispatch({ type: 'SET_FORM_ERROR', payload: msg });
    const clearFormMessages = () => dispatch({ type: 'CLEAR_FORM_MESSAGES' });
    const toggleCustomerForm = () => dispatch({ type: 'TOGGLE_CUSTOMER_FORM' });
    const setEditingCustomer = (customer) => dispatch({ type: 'SET_EDITING_CUSTOMER', payload: customer });
    const closeEditingCustomer = () => dispatch({ type: 'CLOSE_EDITING_CUSTOMER' });
    const setSelectedCustomer = (customer) => dispatch({ type: 'SET_SELECTED_CUSTOMER', payload: customer });
    const closeAddressPanel = () => dispatch({ type: 'CLOSE_ADDRESS_PANEL' });
    const setAddresses = (data) => dispatch({ type: 'SET_ADDRESSES', payload: data });
    const setLoadingAddresses = (val) => dispatch({ type: 'SET_LOADING_ADDRESSES', payload: val });
    const toggleAddressForm = () => dispatch({ type: 'TOGGLE_ADDRESS_FORM' });

    return (
        <CustomerContext.Provider value={{
            customerState,
            setLoading, setError, setCustomers,
            setFormSuccess, setFormError, clearFormMessages,
            toggleCustomerForm, setEditingCustomer, closeEditingCustomer,
            setSelectedCustomer, closeAddressPanel,
            setAddresses, setLoadingAddresses, toggleAddressForm
        }}>
            {children}
        </CustomerContext.Provider>
    );
};

export const useCustomer = () => {
    const ctx = useContext(CustomerContext);
    if (!ctx) throw new Error('useCustomer debe usarse dentro de <CustomerProvider>');
    return ctx;
};