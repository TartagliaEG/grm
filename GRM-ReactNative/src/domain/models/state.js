export default class StateBuilder {
  newState;

  constructor(state) {
    this.newState = { ...state };
    this.state = this.state.bind(this);
    this.set = this.set.bind(this);
  }

  set(key, value) { return ((this.newState[key] = value), this); }
  state() { return this.newState; }
}