package ru.surdasha.cats.presentation.models;

public class State {
    public enum STATUS {SUCCESS, LOADING,ERROR }

    private STATUS status;
    private Throwable error;

    public State success(){
        this.status = STATUS.SUCCESS;
        this.error = null;
        return this;
    }

    public State error(Throwable throwable){
        this.status = STATUS.ERROR;
        this.error = throwable;
        return this;
    }

    public State loading(){
        this.status = STATUS.LOADING;
        this.error = null;
        return this;
    }

    public boolean isError(){
        return status == STATUS.ERROR;
    }

    public boolean isSuccess(){
        return status == STATUS.SUCCESS;
    }

    public boolean isLoading(){
        return status == STATUS.LOADING;
    }
}
