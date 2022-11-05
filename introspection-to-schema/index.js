import React from 'react';
import {render} from 'react-dom';
import {buildClientSchema, printSchema} from 'graphql';

const styles = {
    fontFamily: 'helvetica',
};

const panel = {
    width: '50%',
    float: 'left',
    padding: '20px',
    height: '100vh',
    boxSizing: 'border-box',
    display: 'flex',
    flexDirection: 'column',
};

const fullWidthArea = {
    width: '100%',
    height: '100%',
};

class App extends React.Component {
    constructor() {
        super();
        this.state = { value: '', sdl: '' };
    }

    handleChange(event) {
        let value = event.target.value;
        let sdl = 'test';
        try {
            let data = JSON.parse(value).data;
            sdl = printSchema(buildClientSchema(data));
        } catch (e) {
            sdl = '# Invalid introspection:\n' + e.message;
        }
        this.setState({ value, sdl: sdl });
    }

    render() {
        return (
            <div style={styles}>
                <div style={panel}>
                    <h3> Paste Introspection </h3>
                    <textarea
                        value={this.state.value}
                        onChange={event => this.handleChange(event)}
                        style={fullWidthArea}
                    />
                </div>
                <div style={panel}>
                    <h3> Check IDL </h3>
                    <textarea value={this.state.sdl} style={fullWidthArea} />
                </div>
            </div>
        );
    }
}

render(<App />, document.getElementById('root'));
