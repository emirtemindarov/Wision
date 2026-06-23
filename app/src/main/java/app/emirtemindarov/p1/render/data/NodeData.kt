package app.emirtemindarov.p1.render.data

import app.emirtemindarov.p1.render.EdgeModel
import app.emirtemindarov.p1.render.EdgeType
import app.emirtemindarov.p1.render.NodeModel
import app.emirtemindarov.p1.render.NodeType

// Mock
object NodeData {

    val nodes = mapOf(

        // =========================
        // CORE PLATFORM
        // =========================

        "platform" to NodeModel(
            id = "platform",
            name = "PlatformCore",
            type = NodeType.CLASS,
            childrenIds = listOf("authModule", "billingModule", "messageModule", "analyticsModule")
        ),

        // =========================
        // AUTH MODULE
        // =========================

        "authModule" to NodeModel(
            id = "authModule",
            name = "AuthModule",
            type = NodeType.CLASS,
            childrenIds = listOf("authService", "tokenManager", "authConfig")
        ),

        "authService" to NodeModel(
            id = "authService",
            name = "AuthService",
            type = NodeType.CLASS,
            childrenIds = listOf("login", "logout", "validate")
        ),

        "login" to NodeModel(
            id = "login",
            name = "login",
            type = NodeType.FUNCTION,
            childrenIds = listOf("username", "password")
        ),

        "username" to NodeModel("username", "username", NodeType.VARIABLE),
        "password" to NodeModel("password", "password", NodeType.VARIABLE),

        "logout" to NodeModel("logout", "logout", NodeType.FUNCTION),
        "validate" to NodeModel("validate", "validateToken", NodeType.FUNCTION),

        "tokenManager" to NodeModel(
            id = "tokenManager",
            name = "TokenManager",
            type = NodeType.CLASS,
            childrenIds = listOf("generate", "refresh")
        ),

        "generate" to NodeModel("generate", "generateToken", NodeType.FUNCTION),
        "refresh" to NodeModel("refresh", "refreshToken", NodeType.FUNCTION),

        "authConfig" to NodeModel(
            id = "authConfig",
            name = "AuthConfig",
            type = NodeType.OBJECT,
            childrenIds = listOf("tokenTTL", "secret")
        ),

        "tokenTTL" to NodeModel("tokenTTL", "tokenTTL", NodeType.VARIABLE),
        "secret" to NodeModel("secret", "secretKey", NodeType.VARIABLE),

        // =========================
        // BILLING MODULE
        // =========================

        "billingModule" to NodeModel(
            id = "billingModule",
            name = "BillingModule",
            type = NodeType.CLASS,
            childrenIds = listOf("paymentService", "invoiceService")
        ),

        "paymentService" to NodeModel(
            id = "paymentService",
            name = "PaymentService",
            type = NodeType.CLASS,
            childrenIds = listOf("charge", "refund")
        ),

        "charge" to NodeModel("charge", "chargeUser", NodeType.FUNCTION),
        "refund" to NodeModel("refund", "refundUser", NodeType.FUNCTION),

        "invoiceService" to NodeModel(
            id = "invoiceService",
            name = "InvoiceService",
            type = NodeType.CLASS,
            childrenIds = listOf("createInvoice")
        ),

        "createInvoice" to NodeModel("createInvoice", "createInvoice", NodeType.FUNCTION),

        // =========================
        // MESSAGE MODULE
        // =========================

        "messageModule" to NodeModel(
            id = "messageModule",
            name = "MessagingModule",
            type = NodeType.CLASS,
            childrenIds = listOf("messageService", "queueManager")
        ),

        "messageService" to NodeModel(
            id = "messageService",
            name = "MessageService",
            type = NodeType.CLASS,
            childrenIds = listOf("sendMessage", "receiveMessage")
        ),

        "sendMessage" to NodeModel("sendMessage", "sendMessage", NodeType.FUNCTION),
        "receiveMessage" to NodeModel("receiveMessage", "receiveMessage", NodeType.FUNCTION),

        "queueManager" to NodeModel(
            id = "queueManager",
            name = "QueueManager",
            type = NodeType.CLASS
        ),

        // =========================
        // ANALYTICS MODULE
        // =========================

        "analyticsModule" to NodeModel(
            id = "analyticsModule",
            name = "AnalyticsModule",
            type = NodeType.CLASS,
            childrenIds = listOf("eventTracker", "reportService")
        ),

        "eventTracker" to NodeModel(
            id = "eventTracker",
            name = "EventTracker",
            type = NodeType.CLASS,
            childrenIds = listOf("trackEvent")
        ),

        "trackEvent" to NodeModel("trackEvent", "trackEvent", NodeType.FUNCTION),

        "reportService" to NodeModel(
            id = "reportService",
            name = "ReportService",
            type = NodeType.CLASS,
            childrenIds = listOf("generateReport")
        ),

        "generateReport" to NodeModel("generateReport", "generateReport", NodeType.FUNCTION),

        // =========================
        // SHARED
        // =========================

        "repository" to NodeModel(
            id = "repository",
            name = "Repository",
            type = NodeType.CLASS
        ),

        "networkClient" to NodeModel(
            id = "networkClient",
            name = "NetworkClient",
            type = NodeType.CLASS
        ),

        "logger" to NodeModel(
            id = "logger",
            name = "Logger",
            type = NodeType.CLASS
        ),

        // =========================
        // INTERFACES
        // =========================

        "iAuth" to NodeModel("iAuth", "IAuth", NodeType.INTERFACE),
        "iPayment" to NodeModel("iPayment", "IPayment", NodeType.INTERFACE),
        "iMessaging" to NodeModel("iMessaging", "IMessaging", NodeType.INTERFACE),

        // =========================
        // INHERITANCE
        // =========================

        "baseService" to NodeModel(
            id = "baseService",
            name = "BaseService",
            type = NodeType.CLASS
        ),

        "mockAuthService" to NodeModel(
            id = "mockAuthService",
            name = "MockAuthService",
            type = NodeType.CLASS
        ),

        "testPaymentService" to NodeModel(
            id = "testPaymentService",
            name = "TestPaymentService",
            type = NodeType.CLASS
        )
    )

    val edges = listOf(

        // =========================
        // CORE USES
        // =========================

        EdgeModel("platform", "authModule", EdgeType.USES),
        EdgeModel("platform", "billingModule", EdgeType.USES),
        EdgeModel("platform", "messageModule", EdgeType.USES),
        EdgeModel("platform", "analyticsModule", EdgeType.USES),

        // =========================
        // AUTH
        // =========================

        EdgeModel("authService", "repository", EdgeType.USES),
        EdgeModel("authService", "logger", EdgeType.USES),
        EdgeModel("tokenManager", "authService", EdgeType.USES),

        // цикл
        EdgeModel("authService", "tokenManager", EdgeType.USES),

        // =========================
        // BILLING
        // =========================

        EdgeModel("paymentService", "repository", EdgeType.USES),
        EdgeModel("invoiceService", "paymentService", EdgeType.USES),

        // =========================
        // MESSAGING
        // =========================

        EdgeModel("messageService", "queueManager", EdgeType.USES),
        EdgeModel("queueManager", "networkClient", EdgeType.USES),

        // =========================
        // ANALYTICS
        // =========================

        EdgeModel("eventTracker", "repository", EdgeType.USES),
        EdgeModel("reportService", "eventTracker", EdgeType.USES),

        // =========================
        // CROSS MODULE
        // =========================

        EdgeModel("authService", "messageService", EdgeType.USES),
        EdgeModel("paymentService", "authService", EdgeType.USES),
        EdgeModel("reportService", "paymentService", EdgeType.USES),

        // =========================
        // INTERFACES
        // =========================

        EdgeModel("authService", "iAuth", EdgeType.IMPLEMENTS),
        EdgeModel("paymentService", "iPayment", EdgeType.IMPLEMENTS),
        EdgeModel("messageService", "iMessaging", EdgeType.IMPLEMENTS),

        // =========================
        // INHERITS
        // =========================

        EdgeModel("authService", "baseService", EdgeType.INHERITS),
        EdgeModel("paymentService", "baseService", EdgeType.INHERITS),
        EdgeModel("messageService", "baseService", EdgeType.INHERITS),

        EdgeModel("mockAuthService", "authService", EdgeType.INHERITS),
        EdgeModel("testPaymentService", "paymentService", EdgeType.INHERITS),

        // =========================
        // COMPLEX CHAINS
        // =========================

        EdgeModel("generateReport", "repository", EdgeType.USES),
        EdgeModel("trackEvent", "networkClient", EdgeType.USES),
        EdgeModel("sendMessage", "networkClient", EdgeType.USES),

        // =========================
        // EXTRA CYCLES
        // =========================

        EdgeModel("repository", "logger", EdgeType.USES),
        EdgeModel("logger", "repository", EdgeType.USES)
    )
}