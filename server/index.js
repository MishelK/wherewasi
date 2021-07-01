const express = require('express');
const cors = require('cors');

const app = express();

app.use(express.json());
app.use(cors());
app.options('*', cors());
// Import Routes
const authRoute = require('./routes/auth');

// Route Middlewares
app.use('/api/users', authRoute);

app.get('/', (req, res) => {
    res.send('Unknown')
})

const port = process.env.PORT || 3030;
app.listen(port, () => console.log(`Server is running on ${port}`));