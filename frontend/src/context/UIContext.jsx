import React, { createContext, useReducer, useContext } from 'react';

const UIContext = createContext(null);

const initialUIState = {
    activeTab: 'inventory'
};

function uiReducer(state, action) {
    switch (action.type) {
        case 'SET_TAB':
            return { ...state, activeTab: action.payload };
        default:
            return state;
    }
}

export const UIProvider = ({ children }) => {
    const [uiState, dispatch] = useReducer(uiReducer, initialUIState);

    const setTab = (tab) => dispatch({ type: 'SET_TAB', payload: tab });

    return (
        <UIContext.Provider value={{ uiState, setTab }}>
            {children}
        </UIContext.Provider>
    );
};

export const useUI = () => {
    const ctx = useContext(UIContext);
    if (!ctx) throw new Error('useUI debe usarse dentro de <UIProvider>');
    return ctx;
};