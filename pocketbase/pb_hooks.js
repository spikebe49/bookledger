// PocketBase hooks for BookLedger app

// Auto-generate IDs for new records
onRecordBeforeCreateRequest((e) => {
  if (!e.record.id) {
    e.record.id = crypto.randomUUID();
  }
});

// Update timestamps
onRecordBeforeUpdateRequest((e) => {
  e.record.updated = new Date().toISOString();
});

// Validate category type consistency
onRecordBeforeCreateRequest((e) => {
  if (e.collection.name === 'expenses' || e.collection.name === 'sales') {
    const category = $app.dao().findFirstRecordByData('categories', 'id', e.record.category_id);
    if (category) {
      const expectedType = e.collection.name === 'expenses' ? 'EXPENSE' : 'INCOME';
      if (category.type !== expectedType) {
        throw new BadRequestError('Category type mismatch');
      }
    }
  }
});

// Real-time subscriptions for live updates
onRecordAfterCreateRequest((e) => {
  // Broadcast to connected clients
  $app.realtime().broadcast('bookledger_updates', {
    type: 'create',
    collection: e.collection.name,
    record: e.record
  });
});

onRecordAfterUpdateRequest((e) => {
  // Broadcast to connected clients
  $app.realtime().broadcast('bookledger_updates', {
    type: 'update',
    collection: e.collection.name,
    record: e.record
  });
});

onRecordAfterDeleteRequest((e) => {
  // Broadcast to connected clients
  $app.realtime().broadcast('bookledger_updates', {
    type: 'delete',
    collection: e.collection.name,
    record: { id: e.record.id }
  });
});

// Dashboard statistics endpoint
routerAdd("GET", "/api/dashboard-stats", (c) => {
  const user = c.get("authRecord");
  if (!user) {
    throw new UnauthorizedError("Authentication required");
  }

  const userId = user.id;
  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

  try {
    // Get expenses for current month
    const expenses = $app.dao().findRecordsByFilter(
      'expenses',
      `user_id = "${userId}" && date >= "${startOfMonth.toISOString().split('T')[0]}" && date <= "${endOfMonth.toISOString().split('T')[0]}"`,
      { sort: '-date', limit: 5 }
    );

    // Get sales for current month
    const sales = $app.dao().findRecordsByFilter(
      'sales',
      `user_id = "${userId}" && date >= "${startOfMonth.toISOString().split('T')[0]}" && date <= "${endOfMonth.toISOString().split('T')[0]}"`,
      { sort: '-date', limit: 5 }
    );

    // Calculate totals
    const totalExpenses = expenses.reduce((sum, exp) => sum + exp.amount, 0);
    const totalSales = sales.reduce((sum, sale) => sum + sale.amount, 0);
    const netProfit = totalSales - totalExpenses;

    // Get category breakdowns
    const expenseCategories = {};
    const saleCategories = {};

    expenses.forEach(exp => {
      const category = $app.dao().findFirstRecordByData('categories', 'id', exp.category_id);
      if (category) {
        expenseCategories[category.name] = (expenseCategories[category.name] || 0) + exp.amount;
      }
    });

    sales.forEach(sale => {
      const category = $app.dao().findFirstRecordByData('categories', 'id', sale.category_id);
      if (category) {
        saleCategories[category.name] = (saleCategories[category.name] || 0) + sale.amount;
      }
    });

    return c.json({
      totalExpenses,
      totalSales,
      netProfit,
      expenseCount: expenses.length,
      saleCount: sales.length,
      recentExpenses: expenses.map(exp => ({
        ...exp,
        category: $app.dao().findFirstRecordByData('categories', 'id', exp.category_id)
      })),
      recentSales: sales.map(sale => ({
        ...sale,
        category: $app.dao().findFirstRecordByData('categories', 'id', sale.category_id)
      })),
      expensesByCategory: expenseCategories,
      salesByCategory: saleCategories
    });

  } catch (error) {
    throw new BadRequestError("Failed to fetch dashboard stats: " + error.message);
  }
}, (c) => {
  return c.get("authRecord") != null;
});

// Monthly statistics endpoint
routerAdd("GET", "/api/monthly-stats", (c) => {
  const user = c.get("authRecord");
  if (!user) {
    throw new UnauthorizedError("Authentication required");
  }

  const userId = user.id;
  const year = c.query("year") || new Date().getFullYear();
  const month = c.query("month") || new Date().getMonth() + 1;

  const startOfMonth = new Date(year, month - 1, 1);
  const endOfMonth = new Date(year, month, 0);

  try {
    const expenses = $app.dao().findRecordsByFilter(
      'expenses',
      `user_id = "${userId}" && date >= "${startOfMonth.toISOString().split('T')[0]}" && date <= "${endOfMonth.toISOString().split('T')[0]}"`
    );

    const sales = $app.dao().findRecordsByFilter(
      'sales',
      `user_id = "${userId}" && date >= "${startOfMonth.toISOString().split('T')[0]}" && date <= "${endOfMonth.toISOString().split('T')[0]}"`
    );

    const totalExpenses = expenses.reduce((sum, exp) => sum + exp.amount, 0);
    const totalSales = sales.reduce((sum, sale) => sum + sale.amount, 0);
    const netProfit = totalSales - totalExpenses;

    return c.json({
      month,
      year,
      totalExpenses,
      totalSales,
      netProfit
    });

  } catch (error) {
    throw new BadRequestError("Failed to fetch monthly stats: " + error.message);
  }
}, (c) => {
  return c.get("authRecord") != null;
});
