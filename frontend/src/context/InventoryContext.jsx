import React, { createContext, useReducer, useContext } from 'react';

const InventoryContext = createContext(null);

const initialInventoryState = {
    products: [],
    categories: [],
    loading: false,
    error: '',
    formSuccess: '',
    formError: '',
    showCatForm: false,
    showProdForm: false,
    editingProduct: null,
    editingProductDetails: null,
};

function inventoryReducer(state, action) {
    switch (action.type) {
        case 'SET_LOADING':
            return { ...state, loading: action.payload };
        case 'SET_ERROR':
            return { ...state, error: action.payload };
        case 'SET_PRODUCTS':
            return { ...state, products: action.payload };
        case 'SET_CATEGORIES':
            return { ...state, categories: action.payload };
        case 'SET_FORM_SUCCESS':
            return { ...state, formSuccess: action.payload, formError: '' };
        case 'SET_FORM_ERROR':
            return { ...state, formError: action.payload, formSuccess: '' };
        case 'CLEAR_FORM_MESSAGES':
            return { ...state, formSuccess: '', formError: '' };
        case 'TOGGLE_CAT_FORM':
            return { ...state, showCatForm: !state.showCatForm, showProdForm: false, editingProduct: null, editingProductDetails: null };
        case 'TOGGLE_PROD_FORM':
            return { ...state, showProdForm: !state.showProdForm, showCatForm: false, editingProduct: null, editingProductDetails: null };
        case 'SET_EDITING_PRODUCT':
            return { ...state, editingProduct: action.payload, editingProductDetails: null, showCatForm: false, showProdForm: false };
        case 'SET_EDITING_PRODUCT_DETAILS':
            return { ...state, editingProductDetails: action.payload, editingProduct: null, showCatForm: false, showProdForm: false };
        case 'CLOSE_ALL_FORMS':
            return { ...state, showCatForm: false, showProdForm: false, editingProduct: null, editingProductDetails: null };
        default:
            return state;
    }
}

export const InventoryProvider = ({ children }) => {
    const [inventoryState, dispatch] = useReducer(inventoryReducer, initialInventoryState);

    const setLoading = (val) => dispatch({ type: 'SET_LOADING', payload: val });
    const setError = (msg) => dispatch({ type: 'SET_ERROR', payload: msg });
    const setProducts = (data) => dispatch({ type: 'SET_PRODUCTS', payload: data });
    const setCategories = (data) => dispatch({ type: 'SET_CATEGORIES', payload: data });
    const setFormSuccess = (msg) => dispatch({ type: 'SET_FORM_SUCCESS', payload: msg });
    const setFormError = (msg) => dispatch({ type: 'SET_FORM_ERROR', payload: msg });
    const clearFormMessages = () => dispatch({ type: 'CLEAR_FORM_MESSAGES' });
    const toggleCatForm = () => dispatch({ type: 'TOGGLE_CAT_FORM' });
    const toggleProdForm = () => dispatch({ type: 'TOGGLE_PROD_FORM' });
    const setEditingProduct = (product) => dispatch({ type: 'SET_EDITING_PRODUCT', payload: product });
    const setEditingProductDetails = (product) => dispatch({ type: 'SET_EDITING_PRODUCT_DETAILS', payload: product });
    const closeAllForms = () => dispatch({ type: 'CLOSE_ALL_FORMS' });

    return (
        <InventoryContext.Provider value={{
            inventoryState,
            setLoading, setError, setProducts, setCategories,
            setFormSuccess, setFormError, clearFormMessages,
            toggleCatForm, toggleProdForm,
            setEditingProduct, setEditingProductDetails, closeAllForms
        }}>
            {children}
        </InventoryContext.Provider>
    );
};

export const useInventory = () => {
    const ctx = useContext(InventoryContext);
    if (!ctx) throw new Error('useInventory debe usarse dentro de <InventoryProvider>');
    return ctx;
};